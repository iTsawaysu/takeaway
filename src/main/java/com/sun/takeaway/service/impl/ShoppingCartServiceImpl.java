package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.ShoppingCart;
import com.sun.takeaway.mapper.ShoppingCartMapper;
import com.sun.takeaway.service.ShoppingCartService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

}
