package com.nowcoder.configuration;

import com.nowcoder.interceptor.LoginRequredInterceptor;
import com.nowcoder.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


/**
 * 将拦截器注册，是 Spring 初始化时，注入自定义的拦截器
 */
@Component
public class WendaWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequredInterceptor loginRequredInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        注册拦截器
        registry.addInterceptor(passportInterceptor);
//        添加拦截器拦截条件
        registry.addInterceptor(loginRequredInterceptor).addPathPatterns("/user/*");
        super.addInterceptors(registry);
    }
}
