package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.User;
import com.sun.takeaway.mapper.UserMapper;
import com.sun.takeaway.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

}
