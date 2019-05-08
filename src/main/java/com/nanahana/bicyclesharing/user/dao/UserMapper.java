package com.nanahana.bicyclesharing.user.dao;

import com.nanahana.bicyclesharing.user.entity.User;

/**
 * @Author nana
 * @Date 2019/5/8 14:44
 * @Description 用户mapper类
 */
public interface UserMapper {
    int deleteByPrimaryKey(Long id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}