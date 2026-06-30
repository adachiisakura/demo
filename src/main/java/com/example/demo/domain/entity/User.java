package com.example.demo.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long account;
    private String username;
    @JsonIgnore
    private String password;
    private String mail;
    private String createAt;
    private String sign;
    private String face;
}
