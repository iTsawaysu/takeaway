package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.entity.Orders;
import com.sun.takeaway.mapper.OrdersMapper;
import com.sun.takeaway.service.OrdersService;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */

@Service
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {
}
