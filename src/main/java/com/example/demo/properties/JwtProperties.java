package com.example.demo.properties;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "jwt")
@Component
@Data
@Slf4j
public class JwtProperties {
    private String key;
    private Integer ttl;

    @PostConstruct
    public void JwtCreate(){
        log.info("JWT配置注入成功");
    }

}
