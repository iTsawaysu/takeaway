package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Employee;
import com.sun.takeaway.model.vo.LoginEmployeeVO;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.mapper.EmployeeMapper;
import com.sun.takeaway.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;

import static com.sun.takeaway.constant.EmployeeConstant.AVAILABLE_EMPLOYEE;
import static com.sun.takeaway.constant.EmployeeConstant.EMPLOYEE_LOGIN_STATE;

/**
 * @author sun
 */
@Slf4j
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "iTsawaysu";

    /**
     * 登录
     */
    @Override
    public LoginEmployeeVO login(String username, String password, HttpServletRequest request) {
        // 1. 校验参数
        if (StringUtils.isAnyBlank(username, password)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }

        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());

        // 3. 查询用户是否存在，密码是否正确
        Employee employee = this.lambdaQuery()
                .eq(Employee::getUsername, username)
                .eq(Employee::getPassword, encryptPassword)
                .eq(Employee::getStatus, AVAILABLE_EMPLOYEE)
                .one();
        if (employee == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "该用户不存在或密码错误");
        }

        // 4. 记录用户登录态
        request.getSession().setAttribute(EMPLOYEE_LOGIN_STATE, employee);
        return this.getLoginEmployeeVO(employee);
    }

    /**
     * 获取脱敏的已登录用户信息
     */
    @Override
    public LoginEmployeeVO getLoginEmployeeVO(Employee employee) {
        if (employee == null) {
            return null;
        }
        LoginEmployeeVO loginEmployeeVO = new LoginEmployeeVO();
        BeanUtils.copyProperties(employee, loginEmployeeVO);
        return loginEmployeeVO;
    }

    /**
     * 退出登录
     */
    @Override
    public boolean logout(HttpServletRequest request) {
        if (request.getSession().getAttribute(EMPLOYEE_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        request.getSession().removeAttribute(EMPLOYEE_LOGIN_STATE);
        return false;
    }
}
