package com.example.demo.handler.auth.register;

import com.example.demo.biz.UserAuthBiz;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import com.example.demo.handler.auth.AuthHandler;
import com.example.demo.handler.auth.context.RegisterContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 注册步骤：持久化入库与验证码核销
 */
@Component
public class RegisterProcessHandler implements AuthHandler<RegisterContext> {
    
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private UserAuthBiz userAuthBiz;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handle(RegisterContext context) {
        if (context.getPendingUser() == null) {
            throw new IllegalStateException("待注册用户为空");
        }
        // 显式落库保存
        UserDO savedUser = userDao.save(context.getPendingUser());
        
        // 注册特有的逻辑：核销验证码
        userAuthBiz.consumeRegisterCode(context.getEmail());
        
        // 将结果写回上下文
        context.setResultUser(savedUser);
    }
}
