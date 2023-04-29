package com.sun.takeaway.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Employee;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.model.request.EmployeeLoginRequest;
import com.sun.takeaway.model.vo.LoginEmployeeVO;
import com.sun.takeaway.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author sun
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {

    @Resource
    private EmployeeService employeeService;

    @PostMapping("/login")
    public CommonResult<LoginEmployeeVO> login(@RequestBody EmployeeLoginRequest employeeLoginRequest, HttpServletRequest request) {
        if (employeeLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String username = employeeLoginRequest.getUsername();
        String password = employeeLoginRequest.getPassword();
        if (StringUtils.isAnyBlank(username, password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginEmployeeVO loginEmployeeVO = employeeService.login(username, password, request);
        return CommonResult.success(loginEmployeeVO);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public CommonResult<Boolean> logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = employeeService.logout(request);
        return CommonResult.success(result);
    }

    /**
     * 添加员工
     */
    @PostMapping
    public CommonResult<String> addUser(@RequestBody Employee employee, HttpServletRequest request) {
        return employeeService.addEmployee(employee, request);
    }

    /**
     * 分页查询员工
     */
    @GetMapping("/page")
    public CommonResult<Page> page(@RequestParam("page") int page,
                                   @RequestParam("pageSize") int pageSize,
                                   @RequestParam(value = "name", required = false) String name) {
        return employeeService.page(page, pageSize, name);
    }

    /**
     * 根据员工 id 查询员工信息
     */
    @GetMapping("/{id}")
    public CommonResult<LoginEmployeeVO> getById(@PathVariable("id") Long id) {
        return employeeService.getEmployeeById(id);
    }

    /**
     * 更新员工信息
     */
    @PutMapping
    public CommonResult<String> update(@RequestBody Employee employee, HttpServletRequest request) {
        return employeeService.update(employee, request);
    }
}
