package com.example.demo.service;

import com.example.demo.dao.JsonFileUserDao;
import com.example.demo.entity.User;
import com.example.demo.handler.UserCreateHandlerChain;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户服务层 - 基于文件存储（无第三方依赖）
 */
@Service
public class SimpleFileUserService {

    private final JsonFileUserDao userDao;
    private final UserCreateHandlerChain handlerChain;

    public SimpleFileUserService(JsonFileUserDao userDao, UserCreateHandlerChain handlerChain) {
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
        userDao.reload();
        return userDao.findById(id);
    }

    /**
     * 查询所有用户
     */
    public List<User> getAllUsers() {
        userDao.reload();
        return userDao.findAll();
    }

    /**
     * 更新用户
     */
    public User updateUser(Long id, String username, String email, Integer age) {
        User user = userDao.findById(id);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在，ID：" + id);
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
        return userDao.deleteById(id);
    }

    /**
     * 根据用户名搜索用户
     */
    public List<User> searchUsers(String username) {
        userDao.reload();
        return userDao.findByUsername(username);
    }

    /**
     * 获取用户总数
     */
    public long getUserCount() {
        userDao.reload();
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

    /**
     * 重新加载数据
     */
    public void reloadData() {
        userDao.reload();
    }

    /**
     * 获取存储信息
     */
    public String getStorageInfo() {
        userDao.reload();
        long fileSize = userDao.getDataFileSize();
        int backupCount = userDao.getBackupCount();
        return String.format("数据文件大小: %d 字节, 备份文件数量: %d", fileSize, backupCount);
    }
}
