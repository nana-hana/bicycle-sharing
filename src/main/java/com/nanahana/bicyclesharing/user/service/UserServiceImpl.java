package com.nanahana.bicyclesharing.user.service;

import com.nanahana.bicyclesharing.user.dao.UserMapper;
import com.nanahana.bicyclesharing.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author nana
 * @Date 2019/5/8 17:06
 * @Description 用户具体实现类
 */
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public String login() {
        User user = new User();
        user.setId(1L);
        userMapper.insertSelective(user);
        return null;
    }
}
