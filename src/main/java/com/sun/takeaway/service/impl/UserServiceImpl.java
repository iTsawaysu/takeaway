package com.sun.takeaway.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.takeaway.common.CommonResult;
import com.sun.takeaway.common.ErrorCode;
import com.sun.takeaway.entity.User;
import com.sun.takeaway.exception.BusinessException;
import com.sun.takeaway.exception.ThrowUtils;
import com.sun.takeaway.mapper.UserMapper;
import com.sun.takeaway.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Map;

import static com.sun.takeaway.constant.CommonConstant.AVAILABLE;
import static com.sun.takeaway.constant.LOGINConstant.USER_LOGIN_STATE;

/**
 * @author sun
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 发送验证码
     */
    @Override
    public CommonResult<String> sendMessage(User user, HttpSession session) {
        String phone = user.getPhone();
        if (StringUtils.isBlank(phone)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 生成验证码
        String captcha = RandomUtil.randomNumbers(4);

        // 发送验证码（暂时不接入第三方短信 API 接口）
        System.out.println("captcha = " + captcha);
        session.setAttribute(phone, captcha);

        return CommonResult.success("验证码发送成功");
    }

    /**
     * 登录
     */
    @Override
    public CommonResult<String> login(Map<String, String> map, HttpSession session) {
        String phone = map.get("phone");
        String code = map.get("code");
        String captcha = (String) session.getAttribute(phone);
        if (StringUtils.isAnyBlank(phone, code, captcha)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 验证码比对成功则登录成功（判断是否为新用户）
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.equals(code, captcha)) {
            wrapper.eq(User::getPhone, phone);
            User user = this.getOne(wrapper);
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                user.setStatus(AVAILABLE);
                boolean result = this.save(user);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            }
            session.setAttribute(USER_LOGIN_STATE, user.getId());
            return CommonResult.success("登录成功");
        }
        return CommonResult.error(ErrorCode.NOT_LOGIN_ERROR, "登录失败");
    }
}
