package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.Category;
import com.sun.takeaway.mapper.CategoryMapper;
import com.sun.takeaway.service.CategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

}
