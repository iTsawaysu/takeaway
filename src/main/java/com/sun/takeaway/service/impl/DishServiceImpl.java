package com.sun.takeaway.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Category;
import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.entity.DishFlavor;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.DishMapper;
import com.sun.takeaway.model.dto.DishDTO;
import com.sun.takeaway.service.CategoryService;
import com.sun.takeaway.service.DishFlavorService;
import com.sun.takeaway.service.DishService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.sun.takeaway.constant.CommonConstant.AVAILABLE;
import static com.sun.takeaway.constant.RedisConstant.CACHE_DISH;
import static com.sun.takeaway.constant.RedisConstant.TTL_ONE;

/**
 * @author sun
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Resource
    private DishFlavorService dishFlavorService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增菜品（新增菜品口味）
     */
    @Transactional
    @Override
    public CommonResult<String> add(DishDTO dishDTO) {
        boolean result = this.save(dishDTO);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors = flavors.stream()
                .map(item -> {
                    item.setDishId(dishDTO.getId());
                    return item;
                })
                .collect(Collectors.toList());
        result = dishFlavorService.saveBatch(flavors);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("新增成功");
    }

    /**
     * 分页查询
     */
    @Override
    public CommonResult<Page> page(int page, int pageSize, String name) {
        Page<Dish> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Dish::getName, name);
        }
        wrapper.orderByDesc(Dish::getUpdateTime);
        this.page(pageInfo, wrapper);

        // dish 中只有 categoryId 属性，dishDTO 中多了个 categoryName 属性
        Page<DishDTO> dishDTOPage = new Page<>();
        // 第三个参数 ignoreProperties 表示：拷贝过程中忽略的、无需拷贝的属性
        BeanUtils.copyProperties(pageInfo, dishDTOPage, "records");
        List<DishDTO> dishDTOList = pageInfo.getRecords().stream()
                .map(item -> {
                    DishDTO dishDTO = new DishDTO();
                    BeanUtils.copyProperties(item, dishDTO);
                    Category category = categoryService.getById(item.getCategoryId());
                    if (category != null) {
                        dishDTO.setCategoryName(category.getName());
                    }
                    return dishDTO;
                }).collect(Collectors.toList());
        dishDTOPage.setRecords(dishDTOList);
        return CommonResult.success(dishDTOPage);
    }

    /**
     * 根据菜品 id 查询菜品信息和菜品口味信息
     */
    @Override
    public CommonResult<DishDTO> getDishById(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);
        Dish dish = this.getById(id);
        ThrowUtils.throwIf(dish == null, ErrorCode.NOT_FOUND_ERROR);
        DishDTO dishDTO = new DishDTO();
        BeanUtils.copyProperties(dish, dishDTO);

        // 根据菜品 id 查询该菜品对应的口味
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, id);
        List<DishFlavor> dishFlavorList = dishFlavorService.list(wrapper);
        if (CollectionUtil.isNotEmpty(dishFlavorList)) {
            dishDTO.setFlavors(dishFlavorList);
        }
        return CommonResult.success(dishDTO);
    }

    /**
     * 修改菜品（修改菜品口味）
     */
    @Transactional
    @Override
    public CommonResult<String> update(DishDTO dishDTO) {
        // 1. 更新 Dish
        boolean result = this.updateById(dishDTO);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 2. 将 Dish 对应的 DishFlavour 删除
        LambdaQueryWrapper<DishFlavor> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DishFlavor::getDishId, dishDTO.getId());
        dishFlavorService.remove(wrapper);

        // 3. 新增 Dish 对应的 DishFlavour
        List<DishFlavor> flavors = dishDTO.getFlavors().stream()
                .map(item -> {
                    item.setDishId(dishDTO.getId());
                    return item;
                }).collect(Collectors.toList());
        result = dishFlavorService.saveBatch(flavors);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        stringRedisTemplate.delete(CACHE_DISH + dishDTO.getCategoryId());
        return CommonResult.success("修改成功");
    }

    /**
     * 根据菜品分类 id 查询该分类下的菜品
     */
    @Override
    public CommonResult<List<DishDTO>> getDishesByCategoryId(Long categoryId) {
        // 先从 Redis 中查询，如果查询到则直接返回
        String dishJsonFromRedis = stringRedisTemplate.opsForValue().get(CACHE_DISH + categoryId);
        List<DishDTO> dishDTOList = null;
        if (StringUtils.isNotBlank(dishJsonFromRedis)) {
            dishDTOList = JSONUtil.toList(JSONUtil.parseArray(dishJsonFromRedis), DishDTO.class);
            return CommonResult.success(dishDTOList);
        }

        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(categoryId != null, Dish::getCategoryId, categoryId).eq(Dish::getStatus, AVAILABLE);
        wrapper.orderByAsc(Dish::getSort).orderByAsc(Dish::getUpdateTime);
        List<Dish> dishList = this.list(wrapper);

        dishDTOList = dishList.stream().map(item -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(item, dishDTO);

            // 设置 categoryName
            Category category = categoryService.getById(item.getCategoryId());
            if (category != null) {
                dishDTO.setCategoryName(category.getName());
            }

            // 设置 dishFlavors
            List<DishFlavor> dishFlavorList = dishFlavorService.lambdaQuery().eq(DishFlavor::getDishId, item.getId()).list();
            if (CollectionUtil.isNotEmpty(dishFlavorList)) {
                dishDTO.setFlavors(dishFlavorList);
            }
            return dishDTO;
        }).collect(Collectors.toList());

        // 从数据库中查询到后返回，并将其存入 Redis 中
        stringRedisTemplate.opsForValue().set(CACHE_DISH + categoryId, JSONUtil.toJsonStr(dishDTOList), TTL_ONE, TimeUnit.DAYS);
        return CommonResult.success(dishDTOList);
    }
}
