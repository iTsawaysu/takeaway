package com.sun.takeaway.filter;

import com.alibaba.fastjson.JSON;
import com.sun.takeaway.common.CommonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sun.takeaway.constant.EmployeeConstant.EMPLOYEE_LOGIN_STATE;

/**
 * @author sun
 */
@Slf4j
@Component
@WebFilter(filterName = "loginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    /**
     * 路径比较器
     */
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. 放行登录接口和静态资源
        String requestURI = request.getRequestURI();
        String[] uris = new String[]{"/employee/login", "/backend/**", "/front/**"};

        // 2. 判断本次请求是否需要登录
        boolean checked = this.check(uris, requestURI);
        // 无需登录则直接放行
        if (Boolean.TRUE.equals(checked)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 判断是否登录（登录则直接放行）
        if (request.getSession().getAttribute(EMPLOYEE_LOGIN_STATE) != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 未登录，则通过输出流向 Client 响应数据
        response.getWriter().write(JSON.toJSONString(CommonResult.error(0, "NOT_LOGIN")));
        return;
    }

    /**
     * 判断本次请求是否需要处理
     */
    private boolean check(String[] uris, String requestURI) {
        for (String uri : uris) {
            boolean matched = PATH_MATCHER.match(uri, requestURI);
            if (matched) {
                return true;
            }
        }
        return false;
    }
}