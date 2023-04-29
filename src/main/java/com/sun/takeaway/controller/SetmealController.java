package com.sun.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.model.dto.SetmealDTO;
import com.sun.takeaway.service.SetmealService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author sun
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Resource
    private SetmealService setmealService;

    /**
     * 新增套餐（新增套餐和菜品的关联关系）
     *
     * @return
     */
    @PostMapping
    public CommonResult<String> add(@RequestBody SetmealDTO setmealDTO) {
        if (setmealDTO == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        return setmealService.add(setmealDTO);
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    public CommonResult<Page> page(@RequestParam("page") int page,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "name", required = false) String name) {
        return setmealService.page(page, pageSize, name);
    }

    /**
     * 删除套餐（同时需要删除套餐和菜品的关联关系）
     */
    @DeleteMapping
    public CommonResult<String> deleteByIds(@RequestParam("ids") List<Long> ids) {
        return setmealService.deleteByIds(ids);
    }
}
