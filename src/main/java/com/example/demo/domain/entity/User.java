package com.example.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long account;
    private String username;
    private String password;
    private String mail;
    private String createAt;
}
