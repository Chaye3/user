package com.example.demo.dto.req;

import lombok.Data;

/**
 * 登录参数
 */
@Data
public class LoginParam {
    private String email;
    private String password;
}
