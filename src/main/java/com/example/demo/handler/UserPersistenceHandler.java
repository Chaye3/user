package com.example.demo.handler;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import org.springframework.stereotype.Component;

/**
 * 业务保存落库处理器 - 将用户数据持久化到存储
 */
@Component
public class UserPersistenceHandler implements UserCreateHandler {

    private final UserDao userDao;

    public UserPersistenceHandler(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public void handle(UserCreateContext context) {
        // 使用建造者模式创建用户对象
        User user = User.builder()
            .username(context.getUsername())
            .email(context.getEmail())
            .age(context.getAge())
            .build();

        // 保存到数据库
        User savedUser = userDao.save(user);

        // 将结果设置到上下文中
        context.setResultUser(savedUser);
    }
}
