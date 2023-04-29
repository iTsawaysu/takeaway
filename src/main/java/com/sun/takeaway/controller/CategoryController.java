package com.sun.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Category;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.model.request.CategoryAddRequest;
import com.sun.takeaway.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 新增分类
     */
    @PostMapping
    public CommonResult<String> addCategory(@RequestBody CategoryAddRequest addCategoryRequest) {
        ThrowUtils.throwIf(addCategoryRequest == null, ErrorCode.PARAMS_ERROR);
        return categoryService.addCategory(addCategoryRequest);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public CommonResult<Page> page(@RequestParam("page") int page, @RequestParam("pageSize") int pageSize) {
        return categoryService.page(page, pageSize);
    }

    /**
     * 删除分类（只有该分类未关联任何菜品和套餐时，才能删除）
     */
    @DeleteMapping
    public CommonResult<String> removeById(@RequestParam("id") Long id) {
        return categoryService.deleteCategory(id);
    }

    /**
     * 更新分类
     */
    @PutMapping
    public CommonResult<String> update(@RequestBody Category category) {
        return categoryService.updateCategory(category);
    }

    /**
     * 查询分类（type 为 1 代表菜品分类，type 为 2 代表套餐分类）
     * 此处直接使用 Category 实体接收前端的请求参数 type
     */
    @GetMapping("/list")
    public CommonResult<List<Category>> listByType(Category category) {
        return categoryService.listByType(category);
    }

}
