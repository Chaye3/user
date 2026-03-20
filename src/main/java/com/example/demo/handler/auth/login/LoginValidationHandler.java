package com.example.demo.handler.auth.login;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.LoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录步骤：参数校验
 */
@Component
public class LoginValidationHandler implements AuthHandler<LoginContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(LoginContext context) {
        userAuthBiz.validateLoginParam(context.getEmail(), context.getPassword());
    }
}
