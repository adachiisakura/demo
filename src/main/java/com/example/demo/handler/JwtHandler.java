package com.example.demo.handler;

import com.example.demo.content.UserContent;
import com.example.demo.properties.JwtProperties;
import com.example.demo.utilis.JwtUtili;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 拦截器检验是否登录
 */
@Slf4j
@RequiredArgsConstructor
public class JwtHandler implements HandlerInterceptor {

    private final RedisTemplate redisTemplate;

    private final JwtProperties jwtProperties;

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("[拦截前],拦截到的接口:{}",request.getRequestURL());

        String token = request.getHeader("token");
        if (token == null || token.isEmpty()){
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"未登录，请先登录\"}");
            return false;//不放行
        }

        // 如果Token在黑名单中不放行
        String blacklistKey = "token:blacklist:" + token;
        Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
        if (Boolean.TRUE.equals(isBlacklisted)) {
            log.warn("Token 已在黑名单中，拒绝访问");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"Token 已失效，请重新登录\"}");
            return false;
        }


        Long id =null;
        try{
            Claims claims = JwtUtili.parse_token(jwtProperties.getKey(), token);
             id = Long.valueOf(claims.get("uid").toString());
        }
        catch (ExpiredJwtException e){
            // 1. JWT 已过期，但签名有效，可以从异常中获取 Claims
            Claims claims = e.getClaims();
            id = Long.valueOf(claims.get("uid").toString());
            //查询该token是否在redis里，如果不在，根据用户id删掉redis相关的token，并返回token已过期
            String  userTokenKey = "USER_TOKEN_KEY:" + token;
            if(Boolean.FALSE.equals(redisTemplate.hasKey(userTokenKey))){
                String poolKey = "USER_TOKENS:" + id;
                System.out.println("尝试删除"+poolKey+":"+token);
                redisTemplate.opsForSet().remove(poolKey,token);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);  // 401
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"token已过期\"}");
                return false;//不放行
            }
        }
        catch (JwtException | IllegalArgumentException e) {
            // 其他 JWT 异常（签名错误、格式错误等）
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"无效的token\"}");
            return false;
        }


        UserContent.setCurrentId(id);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("[拦截后]");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        log.info("[完成后回调]");
    }
}
