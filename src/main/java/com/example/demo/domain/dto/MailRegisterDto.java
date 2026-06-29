package com.example.demo.domain.dto;

import lombok.Data;

@Data
public class MailRegisterDto {
    private String vertify_code;
    private String mail;
}
