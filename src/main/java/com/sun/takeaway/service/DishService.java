package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.model.dto.DishDTO;

import java.util.List;

/**
 * @author sun
 */
public interface DishService extends IService<Dish> {
    /**
     * 新增菜品（新增菜品口味）
     */
    CommonResult<String> add(DishDTO dishDTO);

    /**
     * 分页查询
     */
    CommonResult<Page> page(int page, int pageSize, String name);

    /**
     * 根据菜品 id 查询菜品信息和菜品口味信息
     */
    CommonResult<DishDTO> getDishById(Long id);

    /**
     * 修改菜品（修改菜品口味）
     */
    CommonResult<String> update(DishDTO dishDTO);

    /**
     * 根据菜品分类 id 查询该分类下的菜品
     */
    CommonResult<List<DishDTO>> getDishesByCategoryId(Long categoryId);
}
