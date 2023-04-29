package com.sun.takeaway.controller;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.Orders;
import com.sun.takeaway.service.OrdersService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author sun
 */
@RestController
@RequestMapping("/order")
public class OrdersController {

    @Resource
    private OrdersService ordersService;

    /**
     * 用户下单
     */
    @PostMapping("/submit")
    public CommonResult<String> submit(@RequestBody Orders orders) {
        return ordersService.submit(orders);
    }

}
