package com.example.demo.dto.req;

import lombok.Data;

/**
 * 发送验证码参数
 */
@Data
public class SendCodeParam {
    private String email;
}
