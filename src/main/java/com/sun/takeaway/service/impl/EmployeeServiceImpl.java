package com.sun.takeaway.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.Employee;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.EmployeeMapper;
import com.sun.takeaway.model.vo.LoginEmployeeVO;
import com.sun.takeaway.service.EmployeeService;
import com.sun.takeaway.utils.BaseContext;
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
        if (StringUtils.isAnyBlank(username, password)) {
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
        request.getSession().setAttribute(EMPLOYEE_LOGIN_STATE, employee.getId());
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
        BaseContext.remove();
        return false;
    }

    /**
     * 添加员工
     */
    @Override
    public CommonResult<String> addEmployee(Employee employee, HttpServletRequest request) {
        if (employee == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (this.lambdaQuery().eq(Employee::getUsername, employee.getUsername()).one() != null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户名不能重复");
        }

        String defaultPassword = DigestUtils.md5DigestAsHex((SALT + "sun123").getBytes());
        employee.setPassword(defaultPassword);
        employee.setCreateUser(BaseContext.get());
        employee.setUpdateUser(BaseContext.get());
        boolean result = this.save(employee);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return CommonResult.success("添加成功");
    }

    /**
     * 分页查询员工
     */
    @Override
    public CommonResult<Page> page(Integer page, Integer pageSize, String name) {
        // 分页构造器
        Page<Employee> pageInfo = new Page<>(page, pageSize);
        // 条件构造器
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(name)) {
            wrapper.like(Employee::getName, name);
        }
        wrapper.orderByDesc(Employee::getCreateTime);

        // 分页查询
        this.page(pageInfo, wrapper);
        return CommonResult.success(pageInfo);
    }

    /**
     * 更新员工信息
     */
    @Override
    public CommonResult<String> update(Employee employee, HttpServletRequest request) {
        if (employee == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 注意：前端发送过来的 Employee 对象中的 status 值已经发生了反转，只需要修改数据库即可
        employee.setUpdateUser(BaseContext.get());
        boolean result = this.updateById(employee);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return CommonResult.success("修改成功");
    }

    /**
     * 根据员工 id 查询员工信息
     */
    @Override
    public CommonResult<LoginEmployeeVO> getEmployeeById(Long id) {
         Employee employee = getById(id);
         if (employee == null) {
             throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
         }
        LoginEmployeeVO loginEmployeeVO = new LoginEmployeeVO();
        BeanUtils.copyProperties(employee, loginEmployeeVO);
        return CommonResult.success(loginEmployeeVO);
    }
}
