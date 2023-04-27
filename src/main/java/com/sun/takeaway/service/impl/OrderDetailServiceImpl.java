package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.OrderDetail;
import com.sun.takeaway.mapper.OrderDetailMapper;
import com.sun.takeaway.service.OrderDetailService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {

}
