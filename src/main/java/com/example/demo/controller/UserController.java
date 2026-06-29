package com.example.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.demo.domain.entity.Result;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequiredArgsConstructor
@ResponseBody
public class UserController {
    private final UserService userService;

    @GetMapping("/list")
    public Result<String> listAllUser(){
        System.out.println("测试");
        return Result.success("","objectPage");
    }
}
