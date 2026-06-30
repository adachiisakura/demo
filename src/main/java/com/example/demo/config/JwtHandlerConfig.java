package com.example.demo.config;

import com.example.demo.handler.JwtHandler;
import com.example.demo.properties.JwtProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JwtHandlerConfig extends WebMvcConfigurationSupport {

    private final StringRedisTemplate redisTemplate;
    private final JwtProperties jwtProperties;
    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册拦截器");
        registry.addInterceptor(new JwtHandler(redisTemplate,jwtProperties)).addPathPatterns("/**").excludePathPatterns("/api/auth/**");
    }}
