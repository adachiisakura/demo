package com.example.demo.controller;


import com.example.demo.domain.dto.MailRegisterDto;
import com.example.demo.domain.dto.PwdRegisterDto;
import com.example.demo.domain.entity.Result;
import com.example.demo.service.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final JavaMailSenderImpl javaMailSender;

    private final UserService userService;

    private final RedisTemplate redisTemplate;

    //发送验证码到邮箱，存入redis
    @PostMapping("/sendcode2mail")
    public Result<?> sendCodeToMailOrMessage(@RequestBody HashMap<String,String> input) throws MessagingException {
        String email = input.get("email");
        String operate = input.get("operate");
        log.info("处理{}的验证码发送,该验证码用于:{}",email,operate);
        //邮箱正则
        String mail_regex = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";
        if (email.matches(mail_regex)){
            //把验证码发到对方邮箱中
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            String verify_code = UUID.randomUUID().toString().substring(0,6);
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setText("你的验证码为    "+verify_code+"\n有效期为5分钟");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setFrom("398239073@qq.com");
            javaMailSender.send(mimeMessage);
            redisTemplate.opsForValue().set(operate+":"+email,verify_code,Expiration.from(5,TimeUnit.MINUTES));
            return Result.success("邮箱发送成功");
        }
        return Result.success("邮箱发送失败");
    }

    //邮箱注册
    @PostMapping("/register/mail")
    public Result<?> UserAddWithMail(@RequestBody MailRegisterDto mailRegisterDto){
        userService.userAddWithEmail(mailRegisterDto.getMail(),mailRegisterDto.getVertify_code());
        return Result.success("注册成功");
    }

    //用户密码注册
    @PostMapping("/register/pwd")
    public Result<?> UserAdd(@RequestBody PwdRegisterDto pwdRegisterDto){
        log.info("添加用户:{}",pwdRegisterDto);
        userService.userAdd(pwdRegisterDto.getUsername(),pwdRegisterDto.getPassword());
        return Result.success("添加用户成功");
    }

    //用户密码登录
    @PostMapping("/login/pwd")
    public Result<String> userLogin(@RequestBody HashMap<String,Object> request){
        log.info("用户登录：{}",request);
        String token = userService.userLogin(request.get("account"),request.get("password"));
        return Result.success("用户登录成功",token);
    }

    //用户邮箱登录
    @PostMapping("/login/mail")
    public Result<String> userLoginWithMail(@RequestBody MailRegisterDto mailRegisterDto){
        log.info("处理{}的登录",mailRegisterDto.getMail());
        String token = userService.userLoginWithMail(mailRegisterDto.getMail(),mailRegisterDto.getVertify_code());
        return Result.success("用户登录成功",token);
    }

    // 用户登出 删掉redis里的token
    @PostMapping("/logout")
    public Result<?> userLogout(@RequestHeader("auth") String token){
        System.out.println(token);
        userService.logout(token);
        return Result.success("退出成功");
    }
}
