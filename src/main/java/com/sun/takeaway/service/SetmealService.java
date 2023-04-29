package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.Setmeal;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.model.dto.SetmealDTO;

import java.util.List;

/**
 * @author sun
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐（新增套餐和菜品的关联关系）
     */
    CommonResult<String> add(SetmealDTO setmealDTO);

    /**
     * 分页查询
     */
    CommonResult<Page> page(int page, int pageSize, String name);

    /**
     * 删除套餐（同时需要删除套餐和菜品的关联关系）
     */
    CommonResult<String> deleteByIds(List<Long> ids);
}
