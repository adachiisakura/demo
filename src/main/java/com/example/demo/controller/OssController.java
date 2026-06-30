package com.example.demo.controller;

import com.example.demo.properties.OssProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 腾讯云 COS POST Object 直传签名
 * 文档: https://cloud.tencent.com/document/product/436/14690
 */
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
        String endpoint = "https://" + ossProperties.getBucket() + ".cos."
                + ossProperties.getRegion() + ".myqcloud.com";

        // 过期时间 (15分钟)
        long now = Instant.now().getEpochSecond();
        long expire = now + 900;
        Instant expiration = Instant.now().plus(15, ChronoUnit.MINUTES);

        // 构建 Policy
        String policyJson = "{\"expiration\":\"" + expiration + "\"," +
            "\"conditions\":[" +
            "{\"bucket\":\"" + ossProperties.getBucket() + "\"}," +
            "[\"starts-with\",\"$key\",\"" + dir + "\"]," +
            "[\"content-length-range\",1,10485760]" +
            "]}";
        String policy = Base64.getEncoder().encodeToString(
            policyJson.getBytes(StandardCharsets.UTF_8));

        // 腾讯云 COS 签名: q-sign-algorithm=sha1&q-ak=...&q-sign-time=...&q-key-time=...&q-header-list=&q-url-param-list=&q-signature=...
        String keyTime = now + ";" + expire;
        String signKey = hmacSha1Hex(ossProperties.getSecretKey(), keyTime);
        String stringToSign = sha1Hex(policy);
        String signature = hmacSha1Hex(signKey, stringToSign);

        Map<String, String> result = new LinkedHashMap<>();
        result.put("key", key);
        result.put("policy", policy);
        result.put("q-sign-algorithm", "sha1");
        result.put("q-ak", ossProperties.getSecretId());
        result.put("q-key-time", keyTime);
        result.put("q-signature", signature);
        result.put("endpoint", endpoint);
        return result;
    }

    private String sha1Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(digest);
        } catch (Exception e) {
            throw new RuntimeException("SHA-1 failed", e);
        }
    }

    private String hmacSha1Hex(String key, String data) {
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            mac.init(spec);
            return bytesToHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("HMAC-SHA1 failed", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
