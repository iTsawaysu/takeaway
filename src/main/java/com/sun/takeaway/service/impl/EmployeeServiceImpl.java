package com.sun.takeaway.service.impl;

import com.sun.takeaway.entity.Employee;
import com.sun.takeaway.mapper.EmployeeMapper;
import com.sun.takeaway.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @author sun
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

}
