package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.Category;
import com.sun.takeaway.model.request.CategoryAddRequest;

import java.util.List;

/**
 * @author sun
 */
public interface CategoryService extends IService<Category> {

    /**
     * 新增分类
     */
    CommonResult<String> addCategory(CategoryAddRequest addCategoryRequest);


    /**
     * 分页查询
     */
    CommonResult<Page> page(int page, int pageSize);


    /**
     * 删除分类（只有该分类未关联任何菜品和套餐时，才能删除）
     */
    CommonResult<String> deleteCategory(Long id);

    /**
     * 更新分类
     */
    CommonResult<String> updateCategory(Category category);

    /**
     * 查询分类（type 为 1 代表菜品分类，type 为 2 代表套餐分类）
     */
    CommonResult<List<Category>> listByType(String type);
}
