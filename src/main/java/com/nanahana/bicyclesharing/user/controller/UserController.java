package com.nanahana.bicyclesharing.user.controller;

import com.nanahana.bicyclesharing.user.dao.UserMapper;
import com.nanahana.bicyclesharing.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author nana
 * @Date 2019/5/8 10:41
 * @Description 用户Controller类
 */
@RestController
@RequestMapping("user")
public class UserController {

    private final UserMapper userMapper;

    @Autowired
    public UserController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequestMapping("hello")
    public User hello() {
        return userMapper.selectByPrimaryKey(1L);
    }
}
