package com.sun.takeaway.service.impl;

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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Resource
    private SetmealDishService setmealDishService;

    @Resource
    private CategoryService categoryService;

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
        wrapper.eq(Setmeal::getStatus, 1);
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

        return CommonResult.success("删除成功");
    }
}
