package com.example.demo.handler.auth.sendcode;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.SendCodeContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发码步骤：持久化缓存
 */
@Component
public class SendCodePersistenceHandler implements AuthHandler<SendCodeContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(SendCodeContext context) {
        userAuthBiz.saveRegisterCode(context.getEmail(), context.getMockCode(), context.getCodeExpireAt());
    }
}
