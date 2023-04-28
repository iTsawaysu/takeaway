package com.sun.takeaway.service;

import com.sun.takeaway.entity.Employee;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sun.takeaway.model.vo.LoginEmployeeVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sun
 */
public interface EmployeeService extends IService<Employee> {
    /**
     * 登录
     * @param username  用户名
     * @param password  密码
     * @param request   HttpServletRequest
     * @return          LoginEmployeeVO
     */
    LoginEmployeeVO login(String username, String password, HttpServletRequest request);

    /**
     * 获取脱敏的已登录用户信息
     */
    LoginEmployeeVO getLoginEmployeeVO(Employee employee);

    /**
     * 退出登录
     */
    boolean logout(HttpServletRequest request);
}
