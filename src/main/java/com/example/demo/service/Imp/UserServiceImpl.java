package com.example.demo.service.Imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.demo.domain.dto.MailRegisterDto;
import com.example.demo.domain.entity.Result;
import com.example.demo.domain.entity.User;
import com.example.demo.exception.BusinessException;
import com.example.demo.exception.UserLoginException;
import com.example.demo.mapper.UserMapper;
import com.example.demo.properties.JwtProperties;
import com.example.demo.service.UserService;
import com.example.demo.utilis.JwtUtili;
import lombok.RequiredArgsConstructor;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper,User> implements UserService {

    private final UserMapper userMapper;
    private final JwtProperties jwtProperties;
    private final RedisTemplate redisTemplate;

    public void userAdd(String username, String password) {
        String encoding_password = BCrypt.hashpw(String.valueOf(password), BCrypt.gensalt());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        User user = User.builder()
                .username(String.valueOf(username))
                .password(encoding_password)
                .createAt(LocalDateTime.now().format(formatter))
                .build();
        userMapper.insert(user);
    }

    public String userLogin(Object account, Object password) {
        User user = userMapper.selectById(Long.valueOf(String.valueOf(account)));
        if( user == null){
            throw new UserLoginException("用户未找到");
        }
        boolean login_flag=BCrypt.checkpw(String.valueOf(password),user.getPassword());
        if (login_flag){
            return JwtUtili.create_token(user.getAccount(),jwtProperties.getKey(),jwtProperties.getTtl());
        }
        else {
            throw new UserLoginException("账户密码不一致");
        }
    }

    @Override
    public void userAddWithEmail(String mail,String vertify_code) {
        if (mail.matches("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")){
            String redis_vertify_code = redisTemplate.opsForValue().get("register"+mail).toString();
            if(redis_vertify_code == null || !redis_vertify_code.equals(vertify_code)){
                throw new BusinessException("验证码错误或过期");
            }
            redisTemplate.delete(mail);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            try{
                User user = User.builder()
                    .username(UUID.randomUUID().toString().substring(0,10))
                    .password(BCrypt.hashpw(String.valueOf(UUID.randomUUID().toString().substring(0,10)), BCrypt.gensalt()))
                    .mail(mail)
                    .createAt(LocalDateTime.now().format(formatter))
                    .build();
                userMapper.insert(user);
            }
            catch (Exception e)
            {
                //插入失败，手动回滚，恢复验证码（补偿机制）
                redisTemplate.opsForValue().set(mail, redis_vertify_code, Duration.ofMinutes(5));
                throw new BusinessException("注册失败，请重试");
            }
        }
        else {
            throw new BusinessException("邮箱不匹配");
        }
    }

    @Override
    public String userLoginWithMail(String mail,String vertify_code) {
        HashMap<String, Object> stringObjectHashMap = new HashMap<>();
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(User::getMail,mail);
        List<User> users = userMapper.selectList(queryWrapper);
        if(users.isEmpty()){
            throw new BusinessException("没有该用户");
        }
        String login_code =redisTemplate.opsForValue().get("login:" + mail).toString();
        if (login_code.equals(vertify_code)){
            return JwtUtili.create_token(users.get(0).getAccount(),jwtProperties.getKey(),jwtProperties.getTtl());
        }
        else {
            throw new BusinessException("登录失败");
        }
    }

    @Override
    public void logout(String token) {
        // 获取Token在Redis中的Key
        String tokenKey = "USER_TOKEN_KEY:" + token;

        //查询这个Key的剩余过期时间
        Long ttl = redisTemplate.getExpire(tokenKey, TimeUnit.SECONDS);

        // 3. 删除指定Token
        Boolean deleted = redisTemplate.delete(tokenKey);

        if (Boolean.TRUE.equals(deleted) && ttl != null && ttl > 0) {
            // 4. 将Token加入黑名单，过期时间设为刚才查询到的TTL
            String blacklistKey = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", ttl, TimeUnit.SECONDS);
            // 这里存"1"只是占位符，表示在黑名单中
        }
    }
}
