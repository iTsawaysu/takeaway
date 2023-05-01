package com.sun.takeaway.controller;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.entity.User;
import com.sun.takeaway.service.UserService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @author sun
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 发送验证码
     */
    @PostMapping("/sendMsg")
    public CommonResult<String> sendMessage(@RequestBody User user, HttpSession session) {
        return userService.sendMessage(user, session);
    }

    /**
     * 登录
     */
    @PostMapping("/login")
    public CommonResult<String> login(@RequestBody Map<String, String> map, HttpSession session) {
        return userService.login(map, session);
    }

    @CacheEvict(value = "userCache", key = "#id")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id) {
        userService.removeById(id);
    }
}
