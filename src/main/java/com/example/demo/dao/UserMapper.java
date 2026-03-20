package com.example.demo.dao;

import com.example.demo.dos.UserDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    UserDO findById(Long id);

    List<UserDO> findAll();

    void save(UserDO user);

    void update(UserDO user);

    int deleteById(Long id);

    List<UserDO> findByUsername(String username);

    UserDO findByEmail(String email);

    long count();

    void saveAll(List<UserDO> users);

    void deleteAll();
}
