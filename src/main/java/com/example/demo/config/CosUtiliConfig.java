package com.example.demo.config;

import com.example.demo.properties.CosProperties;
import com.example.demo.utilis.CosUtili;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Slf4j
@Configuration
public class CosUtiliConfig {
    private final CosProperties cosProperties;

    @Bean
    public CosUtili createCosClient(){
        log.info("创建云存储工具类");
        return new CosUtili(cosProperties.getSecretId(),cosProperties.getSecretKey(),cosProperties.getRegion(),cosProperties.getBucketname(),cosProperties.getRootSrc());
    }
}
