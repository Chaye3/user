package com.example.demo.handler.auth.register;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.RegisterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册步骤：参数校验
 */
@Component
public class RegisterValidationHandler implements AuthHandler<RegisterContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

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
