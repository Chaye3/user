package com.example.demo.service;

import com.example.demo.dao.UserDao;
import com.example.demo.entity.User;
import com.example.demo.handler.UserCreateHandlerChain;
import org.springframework.stereotype.Service;
import com.example.demo.exception.UserNotFoundException;
import com.example.demo.exception.UserAlreadyExistsException;


import java.util.List;
import java.util.Optional;

/**
 * 用户服务层
 */
@Service
public class UserService {

    private final UserDao userDao;
    private final UserCreateHandlerChain handlerChain;

    public UserService(UserDao userDao, UserCreateHandlerChain handlerChain) {
        this.userDao = userDao;
        this.handlerChain = handlerChain;
    }

    /**
     * 创建用户 - 使用责任链处理器
     */
    public User createUser(String username, String email, Integer age) {
        return handlerChain.execute(username, email, age);
    }

    /**
     * 根据ID查询用户
     */
    public User getUserById(Long id) {
        return Optional.ofNullable(userDao.findById(id))
                .orElseThrow(() -> new UserNotFoundException("用户未找到，ID：" + id));
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    /**
     * 更新用户
     */
    public User updateUser(Long id, String username, String email, Integer age) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new UserNotFoundException("用户不存在，ID：" + id);
        }
        user.setUsername(username);
        user.setEmail(email);
        user.setAge(age);
        return userDao.update(user);
    }

    /**
     * 删除用户
     */
    public boolean deleteUser(Long id) {
        int affectedRows = userDao.deleteById(id);
        if (affectedRows == 0) {
            throw new UserNotFoundException("用户不存在，ID：" + id);
        }
        return true;
    }

    /**
     * 根据用户名搜索用户
     */
    public List<User> searchUsers(String username) {
        return userDao.findByUsername(username);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        return userDao.count();
    }

    /**
     * 批量创建用户
     */
    public List<User> createUsers(List<User> users) {
        return userDao.saveAll(users);
    }

    /**
     * 删除所有用户
     */
    public void deleteAllUsers() {
        userDao.deleteAll();
    }
}
