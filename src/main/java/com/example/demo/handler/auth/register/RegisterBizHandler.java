package com.example.demo.handler.auth.register;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.dos.UserDO;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.RegisterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 注册步骤：业务逻辑与计算
 */
@Component
public class RegisterBizHandler implements AuthHandler<RegisterContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(RegisterContext context) {
        userAuthBiz.validateRegisterCode(context.getEmail(), context.getVerificationCode(), System.currentTimeMillis());
        userAuthBiz.validateEmailNotRegistered(context.getEmail());
        UserDO pendingUser = userAuthBiz.buildRegisterUser(context.getUsername(), context.getEmail(), context.getPassword());
        context.setPendingUser(pendingUser);
    }
}
