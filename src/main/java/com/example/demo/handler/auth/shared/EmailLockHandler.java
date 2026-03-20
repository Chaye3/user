package com.example.demo.handler.auth.shared;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.BaseContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 通用步骤：基于邮箱的并发锁保护
 */
@Component
public class EmailLockHandler implements AuthHandler<BaseContext> {
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    public void handle(BaseContext context) {
        context.setLock(userAuthBiz.acquireEmailLock(context.getEmail()));
    }
}
