package com.example.demo.dao;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(Long id);

    List<User> findAll();

    void save(User user);

    void update(User user);

    void deleteById(Long id);

    List<User> findByUsername(String username);

    User findByEmail(String email);

    long count();

    void saveAll(List<User> users);

    void deleteAll();
}
