package com.nanahana.bicyclesharing.user.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/8 14:44
 * @Description 用户实体
 */
@Data
public class User {
    /**
     * id
     */
    private Long id;
    /**
     * 昵称
     */
    private String nickname;
    /**
     * 手机号
     */
    private String mobile;
    /**
     * 头像
     */
    private String headImg;
    /**
     * 是否实名认证 1： 否 2：已认证
     */
    private Byte verifyFlag;
    /**
     * 是否有效有用 1：有效  2：无效
     */
    private Byte enableFlag;
}