package com.example.demo.dao;

import com.example.demo.dos.UserDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class UserDao {

    @Autowired
    private UserMapper userMapper;

    public UserDO save(UserDO user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        userMapper.save(user);
        return user;
    }

    public UserDO findById(Long id) {
        return userMapper.findById(id);
    }

    public List<UserDO> findAll() {
        return userMapper.findAll();
    }

    public UserDO update(UserDO user) {
        user.setUpdateTime(LocalDateTime.now());
        userMapper.update(user);
        return user;
    }

    public int deleteById(Long id) {
        return userMapper.deleteById(id);
    }

    public List<UserDO> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    public UserDO findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    public long count() {
        return userMapper.count();
    }

    public boolean existsById(Long id) {
        return userMapper.findById(id) != null;
    }

    public List<UserDO> saveAll(List<UserDO> users) {
        for (UserDO user : users) {
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
