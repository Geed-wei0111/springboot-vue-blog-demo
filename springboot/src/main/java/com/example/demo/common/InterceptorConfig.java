package com.example.demo.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 拦截插件
 * @author Geed
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截所有请求时，除了这些不拦截
        registry.addInterceptor(jwtInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/user/login", "/user/register", "/**/export", "/**/import", "/files/**", "/news/latest"
                        , "/news/random", "/tags/new", "/news/archive", "/news/mostlike", "/news/mostcollect", "/mail/**")
                //swagger3 的路径
                .excludePathPatterns("/swagger**/**")
                .excludePathPatterns("/webjars/**")
                .excludePathPatterns("/v3/**")
                .excludePathPatterns("/doc.html");
    }

    @Bean
    public JwtInterceptor jwtInterceptor() {
        return new JwtInterceptor();
    }
}
