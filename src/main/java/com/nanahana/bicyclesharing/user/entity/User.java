package com.nanahana.bicyclesharing.user.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/8 14:44
 * @Description 用户实体
 */
@Data
public class User {
    private Long id;
    private String nickname;
    private String mobile;
    private String headImg;
    private Byte verifyFlag;
    private Byte enableFlag;
}