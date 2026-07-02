package com.example.demo.controller;


import com.example.demo.domain.entity.Result;
import com.example.demo.utilis.CosUtili;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssController {
    private final CosUtili cosUtili;

    @PostMapping("")
    public Result<?> uploadFile(MultipartFile file){
        try {
            String string = cosUtili.upload(file);
            System.out.println(string);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Result.success("上传成功");
    }
}
