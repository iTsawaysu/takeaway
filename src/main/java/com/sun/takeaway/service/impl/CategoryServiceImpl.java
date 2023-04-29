package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Category;
import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.entity.Setmeal;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.CategoryMapper;
import com.sun.takeaway.model.request.CategoryAddRequest;
import com.sun.takeaway.service.CategoryService;
import com.sun.takeaway.service.DishService;
import com.sun.takeaway.service.SetmealService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Resource
    private DishService dishService;

    @Resource
    private SetmealService setmealService;

    /**
     * 新增分类
     */
    @Override
    public CommonResult<String> addCategory(CategoryAddRequest addCategoryRequest) {
        String name = addCategoryRequest.getName();
        Integer type = addCategoryRequest.getType();
        Integer sort = addCategoryRequest.getSort();
        if (StringUtils.isAnyBlank(name, type.toString(), sort.toString())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Category category = new Category();
        BeanUtils.copyProperties(addCategoryRequest, category);
        boolean result = this.save(category);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("新增成功");
    }

    /**
     * 分页查询
     */
    @Override
    public CommonResult<Page> page(int page, int pageSize) {
        Page<Category> pageInfo = new Page<>(page, pageSize);
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSort);
        this.page(pageInfo, wrapper);
        return CommonResult.success(pageInfo);
    }

    /**
     * 删除分类（只有该分类未关联任何菜品和套餐时，才能删除）
     */
    @Override
    public CommonResult<String> deleteCategory(Long id) {
        Long dishCount = dishService.lambdaQuery().eq(Dish::getCategoryId, id).count();
        if (dishCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前分类已关联某个菜品，无法删除");
        }
        Long setmealCount = setmealService.lambdaQuery().eq(Setmeal::getCategoryId, id).count();
        if (setmealCount > 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "当前分类已关联某个套餐，无法删除");
        }
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("删除成功");
    }

    /**
     * 更新分类
     */
    @Override
    public CommonResult<String> updateCategory(Category category) {
        ThrowUtils.throwIf(category == null, ErrorCode.PARAMS_ERROR);
        boolean result = this.updateById(category);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("更新成功");
    }

    /**
     * 查询分类（type 为 1 代表菜品分类，type 为 2 代表套餐分类）
     */
    @Override
    public CommonResult<List<Category>> listByType(Category category) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (category != null) {
            if (category.getType() != null) {
                wrapper.eq(Category::getType, category.getType());
            }
        }
        wrapper.orderByAsc(Category::getSort).orderByAsc(Category::getUpdateTime);
        List<Category> categoryList = this.list(wrapper);
        ThrowUtils.throwIf(categoryList.isEmpty(), ErrorCode.NOT_FOUND_ERROR);
        return CommonResult.success(categoryList);
    }
}
