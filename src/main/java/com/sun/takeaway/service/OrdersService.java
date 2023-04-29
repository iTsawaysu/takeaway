package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.Orders;

/**
 * @author sun
 */
public interface OrdersService extends IService<Orders> {

    /**
     * 用户下单
     */
    CommonResult<String> submit(Orders orders);
}
