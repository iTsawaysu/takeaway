package com.sun.takeaway.service;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author sun
 */
public interface UserService extends IService<User> {

    /**
     * 发送验证码
     */
    CommonResult<String> sendMessage(User user, HttpSession session);

    /**
     * 登录
     */
    CommonResult<String> login(Map<String, String> map, HttpSession session);
}
