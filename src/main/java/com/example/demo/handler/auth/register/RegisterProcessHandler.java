package com.example.demo.handler.auth.register;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.RegisterContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 注册步骤：持久化入库与验证码核销
 */
@Component
@RequiredArgsConstructor
public class RegisterProcessHandler implements AuthHandler<RegisterContext> {

    private final UserDao userDao;
    private final UserAuthBiz userAuthBiz;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handle(RegisterContext context) {
        if (context.getPendingUser() == null) {
            throw new IllegalStateException("待注册用户为空");
        }

        // [事务内] 数据库持久化
        UserDO savedUser = userDao.save(context.getPendingUser());
        context.setResultUser(savedUser);

        // [事务外] 事务提交后核销验证码，避免事务回滚导致验证码丢失
        String email = context.getEmail();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                userAuthBiz.consumeRegisterCode(email);
            }
        });
    }
}
