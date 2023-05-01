package com.sun.takeaway.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Category;
import com.sun.takeaway.entity.Setmeal;
import com.sun.takeaway.entity.SetmealDish;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.SetmealMapper;
import com.sun.takeaway.model.dto.SetmealDTO;
import com.sun.takeaway.service.CategoryService;
import com.sun.takeaway.service.SetmealDishService;
import com.sun.takeaway.service.SetmealService;
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
import static com.sun.takeaway.constant.RedisConstant.CACHE_SETMEAL;
import static com.sun.takeaway.constant.RedisConstant.TTL_ONE;

/**
 * @author sun
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 新增套餐（新增套餐和菜品的关联关系）
     */
    @Override
    @Transactional
    public CommonResult<String> add(SetmealDTO setmealDTO) {
        boolean result = this.save(setmealDTO);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes().stream()
                .map(item -> {
                    item.setSetmealId(setmealDTO.getId());
                    return item;
                }).collect(Collectors.toList());
        result = setmealDishService.saveBatch(setmealDishList);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("添加成功");
    }

    /**
     * 分页查询
     */
    @Override
    public CommonResult<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Setmeal::getName, name);
        }
        wrapper.orderByDesc(Setmeal::getUpdateTime);
        this.page(pageInfo, wrapper);

        // setmeal 中只有 categoryId 属性，setmealDTO 中多了个 categoryName 属性
        Page<SetmealDTO> setmealDTOPage = new Page<>();
        // 第三个参数 ignoreProperties 表示：拷贝过程中忽略的、无需拷贝的属性
        BeanUtils.copyProperties(pageInfo, setmealDTOPage, "records");
        List<SetmealDTO> dishDTOList = pageInfo.getRecords().stream()
                .map(item -> {
                    SetmealDTO dishDTO = new SetmealDTO();
                    BeanUtils.copyProperties(item, dishDTO);
                    Category category = categoryService.getById(item.getCategoryId());
                    if (category != null) {
                        dishDTO.setCategoryName(category.getName());
                    }
                    return dishDTO;
                }).collect(Collectors.toList());
        setmealDTOPage.setRecords(dishDTOList);
        return CommonResult.success(setmealDTOPage);
    }

    /**
     * 删除套餐（同时需要删除套餐和菜品的关联关系）
     */
    @Transactional
    @Override
    public CommonResult<String> deleteByIds(List<Long> ids) {
        if (ids.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 1. 售卖中的套餐无法删除
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Setmeal::getId, ids);
        wrapper.eq(Setmeal::getStatus, AVAILABLE);
        long count = this.count(wrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "售卖中，无法删除");
        }

        // 2. 删除已下架套餐
        boolean result = this.removeByIds(ids);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 3. 删除该套餐对应的菜品的关联关系
        LambdaQueryWrapper<SetmealDish> setmealDishWrapper = new LambdaQueryWrapper<>();
        setmealDishWrapper.in(SetmealDish::getSetmealId, ids);
        result = setmealDishService.remove(setmealDishWrapper);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 4. 删除 Redis 缓存中的套餐
        for (Long id : ids) {
            stringRedisTemplate.delete(CACHE_SETMEAL + id);
        }
        return CommonResult.success("删除成功");
    }

    /**
     * 根据套餐分类 id 查询该分类下的套餐
     */
    @Override
    public CommonResult<List<Setmeal>> getSetmealListByCategoryId(Setmeal setmeal) {
        ThrowUtils.throwIf(setmeal == null, ErrorCode.PARAMS_ERROR);

        // 先从 Redis 中获取数据，获取到则直接返回
        String setmealJsonFromRedis = stringRedisTemplate.opsForValue().get(CACHE_SETMEAL + setmeal.getCategoryId());
        List<Setmeal> setmealList = null;
        if (StringUtils.isNotBlank(setmealJsonFromRedis)) {
            setmealList = JSONUtil.toList(JSONUtil.parseArray(setmealJsonFromRedis), Setmeal.class);
            return CommonResult.success(setmealList);
        }

        // 从数据库中获取数据后返回
        setmealList = this.lambdaQuery()
                .eq(setmeal.getCategoryId() != null, Setmeal::getCategoryId, setmeal.getCategoryId())
                .eq(Setmeal::getStatus, setmeal.getStatus())
                .orderByDesc(Setmeal::getUpdateTime).list();

        // 将从数据库中查询到的数据写入到 Redis 中
        stringRedisTemplate.opsForValue().set(CACHE_SETMEAL + setmeal.getCategoryId(), JSONUtil.toJsonStr(setmealList), TTL_ONE, TimeUnit.DAYS);
        return CommonResult.success(setmealList);
    }
}
