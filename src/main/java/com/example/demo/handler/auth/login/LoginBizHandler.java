package com.example.demo.handler.auth.login;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.dos.UserDO;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.LoginContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 登录步骤：业务校验与计算
 */
@Component
public class LoginBizHandler implements AuthHandler<LoginContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(LoginContext context) {
        UserDO user = userAuthBiz.findUserByEmail(context.getEmail());
        userAuthBiz.validatePassword(user, context.getPassword());
        context.setResultUser(user);
    }
}
