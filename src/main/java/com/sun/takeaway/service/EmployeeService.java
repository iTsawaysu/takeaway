package com.sun.takeaway.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
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

    /**
     * 添加员工
     */
    CommonResult<String> addEmployee(Employee employee, HttpServletRequest request);

    /**
     * 分页查询
     */
    CommonResult<Page> page(Integer page, Integer pageSize, String name);

    /**
     * 更新员工信息
     */
    CommonResult<String> update(Employee employee, HttpServletRequest request);

    /**
     *
     */
    CommonResult<LoginEmployeeVO> getEmployeeById(Long id);
}
