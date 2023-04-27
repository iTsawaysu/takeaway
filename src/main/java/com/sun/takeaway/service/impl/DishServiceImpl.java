package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.Dish;
import com.sun.takeaway.mapper.DishMapper;
import com.sun.takeaway.service.DishService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

}
