package com.sun.takeaway.controller;

import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.model.request.EmployeeLoginRequest;
import com.sun.takeaway.model.vo.LoginEmployeeVO;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.service.EmployeeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
