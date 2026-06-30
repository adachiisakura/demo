package com.example.demo.controller;

import com.example.demo.properties.OssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssController {

    private final OssProperties ossProperties;

    @GetMapping("/upload-token")
    public Map<String, String> getUploadToken() {
        String dir = ossProperties.getKeyPrefix();
        String filename = UUID.randomUUID().toString().replace("-", "") + ".jpg";
        String key = dir + filename;
        Instant expiration = Instant.now().plus(30, ChronoUnit.MINUTES);

        // 构建 policy
        String policy = Base64.getEncoder().encodeToString(
            ("{\"expiration\":\"" + expiration + "\"," +
             "\"conditions\":[" +
             "[\"content-length-range\",1,10485760]," +
             "[\"starts-with\",\"$key\",\"" + dir + "\"]" +
             "]}").getBytes(StandardCharsets.UTF_8)
        );

        // HMAC-SHA1 签名
        String signature = hmacSha1(policy, ossProperties.getAccessKeySecret());

        Map<String, String> result = new LinkedHashMap<>();
        result.put("accessKeyId", ossProperties.getAccessKeyId());
        result.put("policy", policy);
        result.put("signature", signature);
        result.put("key", key);
        result.put("endpoint", ossProperties.getEndpoint());
        result.put("bucket", ossProperties.getBucket());
        return result;
    }

    private String hmacSha1(String data, String secret) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            mac.init(spec);
            return Base64.getEncoder().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA1 failed", e);
        }
    }
}
