package com.sun.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.model.dto.DishDTO;
import com.sun.takeaway.service.DishService;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@RestController
@RequestMapping("/dish")
public class DishController {

    @Resource
    private DishService dishService;

    /**
     * 新增菜品（新增菜品口味）
     */
    @PostMapping
    public CommonResult<String> add(@RequestBody DishDTO dishDTO) {
        if (dishDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return dishService.add(dishDTO);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public CommonResult<Page> page(@RequestParam("page") int page,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "name", required = false) String name) {
        return dishService.page(page, pageSize, name);
    }

    /**
     * 根据菜品 id 查询菜品信息和菜品口味信息
     */
    @GetMapping("/{id}")
    public CommonResult<DishDTO> getDishById(@PathVariable("id") Long id) {
        return dishService.getDishById(id);
    }

    /**
     * 修改菜品（修改菜品口味）
     */
    @PutMapping
    public CommonResult<String> update(@RequestBody DishDTO dishDTO) {
        if (dishDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return dishService.update(dishDTO);
    }

    /**
     * 根据菜品分类 id 查询该分类下的菜品
     */
    @GetMapping("/list")
    public CommonResult<List<Dish>> getDishesByCategoryId(@Param("categoryId") Long categoryId) {
        return dishService.getDishesByCategoryId(categoryId);
    }

}
