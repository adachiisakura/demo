package com.example.demo.controller;


import com.example.demo.content.UserContent;
import com.example.demo.domain.dto.UserFormDto;
import com.example.demo.domain.entity.Result;
import com.example.demo.domain.entity.User;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@ResponseBody
@Slf4j
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public Result<User> currentUser(){
        Long currentId = UserContent.getCurrentId();
        log.info("查询用户信息，账号为：{}",currentId);
        User user = userService.queryUser(currentId);
        return Result.success("查询成功",user);
    }

    @PutMapping("/profile")
    public Result<?> updateUser(@RequestBody UserFormDto userFormDto){
        Long currentId = UserContent.getCurrentId();
        User user = new User();
        BeanUtils.copyProperties(userFormDto,user);
        userService.updateUser(currentId,user);
        return Result.success("修改成功");
    }
}
