package com.example.demo.dto.req;

import lombok.Data;

/**
 * 注册参数
 */
@Data
public class RegisterParam {
    private String username;
    private String email;
    private String password;
    private String verificationCode;
}
