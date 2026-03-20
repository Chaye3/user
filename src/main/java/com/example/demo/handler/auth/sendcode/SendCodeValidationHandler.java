package com.example.demo.handler.auth.sendcode;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.SendCodeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发码步骤：参数校验
 */
@Component
public class SendCodeValidationHandler implements AuthHandler<SendCodeContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(SendCodeContext context) {
        userAuthBiz.validateEmail(context.getEmail());
    }
}
