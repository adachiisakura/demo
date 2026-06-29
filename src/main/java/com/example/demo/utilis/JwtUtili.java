package com.example.demo.utilis;


import io.jsonwebtoken.Jwts;
import com.example.demo.properties.JwtProperties;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


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
}
