package com.example.demo.handler.auth.register;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.RegisterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 注册步骤：参数校验
 */
@Component
@RequiredArgsConstructor
public class RegisterValidationHandler implements AuthHandler<RegisterContext> {

    private final UserAuthBiz userAuthBiz;

    @Override
    public void handle(RegisterContext context) {
        userAuthBiz.validateRegisterParam(
                context.getUsername(),
                context.getEmail(),
                context.getPassword(),
                context.getVerificationCode()
        );
    }
}
