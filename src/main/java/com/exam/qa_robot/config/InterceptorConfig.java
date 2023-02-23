package com.exam.qa_robot.config;

import com.exam.qa_robot.config.Interceptor.JwtInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration//配置类
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")    // 拦截所有请求，通过判断 token是否合法 决定是否需要登录
                .excludePathPatterns("/book","/file/**","/categorys","/corpus/**","/keywords/**","/user/login","/user","/user/register","/user/**","/question/**","/swagger-ui","/swagger-ui/**");//需要放行的方法
//                .excludePathPatterns("/user/login","/user/register","/user/import","/user/export");//需要放行的方法
    }

    @Bean
    public JwtInterceptor jwtInterceptor(){
        return new JwtInterceptor();
    }

}
