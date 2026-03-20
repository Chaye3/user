package com.example.demo.handler.auth.context;

import lombok.Getter;
import lombok.Setter;

/**
 * 发送验证码上下文
 */
@Setter
@Getter
public class SendCodeContext extends BaseContext {
    private String mockCode;
    private Long codeExpireAt;

}
