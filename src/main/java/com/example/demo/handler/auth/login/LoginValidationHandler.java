package com.example.demo.handler.auth.login;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.LoginContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 登录步骤：参数校验
 */
@Component
@RequiredArgsConstructor
public class LoginValidationHandler implements AuthHandler<LoginContext> {

    private final UserAuthBiz userAuthBiz;

    @Override
    public void handle(LoginContext context) {
        userAuthBiz.validateLoginParam(context.getEmail(), context.getPassword());
    }
}
