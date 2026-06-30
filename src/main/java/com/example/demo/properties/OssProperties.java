package com.example.demo.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "oss")
public class OssProperties {
    /** 地域，如 ap-guangzhou */
    private String region;
    /** 存储桶名，如 example-1250000000 */
    private String bucket;
    /** SecretId */
    private String secretId;
    /** SecretKey */
    private String secretKey;
    /** 上传目录前缀 */
    private String keyPrefix = "avatars/";
}
