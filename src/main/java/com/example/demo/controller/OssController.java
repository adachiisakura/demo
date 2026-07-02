package com.example.demo.controller;


import com.example.demo.domain.entity.Result;
import com.example.demo.properties.CosProperties;
import com.example.demo.utilis.CosUtili;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@RestController
@RequestMapping("/api/oss")
@RequiredArgsConstructor
public class OssController {
    private final CosUtili cosUtili;
    private final CosProperties cosProperties;

    @PostMapping("")
    public Result<String> uploadFile(MultipartFile file) {
        try {
            String key = cosUtili.upload(file);
            String url = "https://" + cosProperties.getBucketname() + ".cos."
                    + cosProperties.getRegion() + ".myqcloud.com/" + key;
            return Result.success("上传成功", url);
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }

    @DeleteMapping("")
    public Result<?> deleteFile(@RequestParam String url) {
        try {
            String prefix = "https://" + cosProperties.getBucketname() + ".cos."
                    + cosProperties.getRegion() + ".myqcloud.com/";
            if (!url.startsWith(prefix)) {
                return Result.error("非本存储桶的图片，无法删除");
            }
            String key = url.substring(prefix.length());
            cosUtili.deleteObject(key);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
