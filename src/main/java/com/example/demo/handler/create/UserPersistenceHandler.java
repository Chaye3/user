package com.example.demo.handler.create;

import com.example.demo.context.UserCreateContext;
import com.example.demo.dao.UserDao;
import com.example.demo.dos.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * 业务保存落库处理器 - 将用户数据持久化到存储
 */
@Component
public class UserPersistenceHandler implements UserCreateHandler {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void handle(UserCreateContext context) {
        // 1. 使用建造者模式创建待持久化的用户对象
        UserDO user = UserDO.builder()
            .username(context.getUsername())
            .email(context.getEmail())
            .age(context.getAge())
            .build();
            
        // 2. 显式调用数据访问层进行落库保存
        UserDO savedUser = userDao.save(user);

        // 3. 将结果写回 Context
        context.setResultUser(savedUser);
    }
}
