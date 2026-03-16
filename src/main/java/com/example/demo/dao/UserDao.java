package com.example.demo.dao;

import com.example.demo.entity.User;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserDao {

    private final UserMapper userMapper;

    public UserDao(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User save(User user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.save(user);
        return user;
    }

    public User findById(Long id) {
        return userMapper.findById(id);
    }

    public List<User> findAll() {
        return userMapper.findAll();
    }

    public User update(User user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    public boolean deleteById(Long id) {
        userMapper.deleteById(id);
        return true;
    }

    public List<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public User findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public long count() {
        return userMapper.count();
    }

    public boolean existsById(Long id) {
        return userMapper.findById(id) != null;
    }

    public List<User> saveAll(List<User> users) {
        for (User user : users) {
            if (user.getCreateTime() == null) {
                user.setCreateTime(LocalDateTime.now());
            }
            user.setUpdateTime(LocalDateTime.now());
        }
        userMapper.saveAll(users);
        return users;
    }

    public void deleteAll() {
        userMapper.deleteAll();
    }
}
