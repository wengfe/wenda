package com.nowcoder.interceptor;


import com.nowcoder.model.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Component
public class LoginRequredInterceptor implements HandlerInterceptor {
    private static final Logger logger = LoggerFactory.getLogger(HandlerInterceptor.class);


    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
//        当用户未注册，携带当前 url 跳转登录页
        if (hostHolder.getUser() == null){
            httpServletResponse.sendRedirect("/reglogin?next=" + httpServletRequest.getRequestURL());
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
