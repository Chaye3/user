package com.example.demo.handler.auth.sendcode;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.SendCodeContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 发码步骤：参数校验
 */
@Component
@RequiredArgsConstructor
public class SendCodeValidationHandler implements AuthHandler<SendCodeContext> {

    private final UserAuthBiz userAuthBiz;

    @Override
    public void handle(SendCodeContext context) {
        userAuthBiz.validateEmail(context.getEmail());
    }
}
