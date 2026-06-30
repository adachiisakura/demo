package com.example.demo.utilis;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;


@RequiredArgsConstructor
public class JwtUtili {

    public static String create_token(Long userAccount,String key,Integer ttl){
        HashMap claim = new HashMap<String, Long>();
        //生成符合要求的secretkey
        //SecretKey secretKey = Keys.secretKeyFor(io.jsonwebtoken.SignatureAlgorithm.HS256);
        //String encodedKey = Base64.getEncoder().encodeToString(secretKey.getEncoded());
        //System.out.println("生成的 Base64 密钥: " + encodedKey);

        claim.put("uid",userAccount);
        return Jwts.builder().signWith(SignatureAlgorithm.HS256, key.getBytes(StandardCharsets.UTF_8))
                .setClaims(claim)
                .setExpiration(new Date(System.currentTimeMillis()+ ttl))
                .compact();
    }

    public static Claims parse_token(String key, String token) {
        // 将字符串密钥转换为 SecretKey
        SecretKey secretKey = Keys.hmacShaKeyFor(key.getBytes(StandardCharsets.UTF_8));

        return Jwts.parser()
                .verifyWith(secretKey)  // 传入 SecretKey 对象
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
