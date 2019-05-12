package com.nanahana.bicyclesharing.user.dao;

import com.nanahana.bicyclesharing.user.entity.User;

/**
 * @Author nana
 * @Date 2019/5/8 14:44
 * @Description 用户mapper类
 */
public interface UserMapper {
    /**
     * 根据手机号搜索用户信息
     *
     * @param mobile 手机号
     * @return 返回用户信息
     */
    User selectByMobile(String mobile);

    /**
     * 根据主键删除信息
     *
     * @param id 主键
     * @return 返回删除成功与否
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 插入信息
     *
     * @param record 需要插入的信息
     * @return 返回插入成功与否
     */
    int insertSelective(User record);

    /**
     * 根据id查询信息
     *
     * @param id 需要查询的id
     * @return 返回查询的结果
     */
    User selectByPrimaryKey(Long id);

    /**
     * 根据主键更新信息
     *
     * @param record 需要更新的信息
     * @return 返回更新成功与否
     */
    int updateByPrimaryKeySelective(User record);
}