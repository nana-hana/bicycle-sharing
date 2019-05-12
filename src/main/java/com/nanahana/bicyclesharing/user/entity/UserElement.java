package com.nanahana.bicyclesharing.user.entity;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author nana
 * @Date 2019/5/9 17:20
 * @Description 用户基本信息
 */
@Data
public class UserElement {
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 用户手机号
     */
    private String mobile;
    /**
     * 用户token
     */
    private String token;
    /**
     * 用户手机平台
     */
    private String platform;
    /**
     * 推送的用户id
     */
    private String pushUserId;
    /**
     * 推送的用户群体id
     */
    private String pushChannelId;


    /**
     * 对象转map
     *
     * @return 返回转换成的map
     */
    public Map<String, String> toMap() {
        int length = 6;
        Map<String, String> map = new HashMap<>(length);
        map.put("userId", userId.toString());
        map.put("mobile", mobile);
        map.put("token", token);
        map.put("platform", platform);
        if (pushUserId != null) {
            map.put("pushUserId", pushUserId);
        }
        if (pushChannelId != null) {
            map.put("pushChannelId", pushChannelId);
        }
        return map;
    }

    /**
     * map转对象
     *
     * @param map 需要转换成对象的map
     * @return 返回转换成的对象
     */
    public static UserElement toObject(Map<String, String> map) {
        UserElement userElement = new UserElement();
        userElement.setUserId(Long.parseLong(map.get("userId")));
        userElement.setMobile(map.get("mobile"));
        userElement.setToken(map.get("token"));
        userElement.setPlatform(map.get("platform"));
        userElement.setPushUserId(map.get("pushUserId"));
        userElement.setPushChannelId(map.get("pushChannelId"));
        return userElement;
    }

}
