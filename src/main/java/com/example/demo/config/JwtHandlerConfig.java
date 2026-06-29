package com.example.demo.config;

import com.example.demo.handler.JwtHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
@Slf4j
public class JwtHandlerConfig extends WebMvcConfigurationSupport {
    @Autowired  // 注入 Redis 模板
    private StringRedisTemplate redisTemplate;

    public void addInterceptors(InterceptorRegistry registry) {
        log.info("注册拦截器");
        registry.addInterceptor(new JwtHandler(redisTemplate)).addPathPatterns("/**").excludePathPatterns("/api/auth/**");
    }}
