package com.nanahana.bicyclesharing.user.entity;

import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/9 12:37
 * @Description 加密过的用户登录信息及密匙
 */
@Data
public class LoginInfo {
    /**
     * 登录信息密文
     */
    private String data;
    /**
     * RSA加密的AES密匙（加密过的公钥）
     */
    private String key;
}
