package com.nanahana.bicyclesharing.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Random;

/**
 * @Author nana
 * @Date 2019/5/11 15:28
 * @Description 随机数类
 */
public class RandomNumberCode {

    /**
     * 随机校验验证码
     *
     * @return 返回随机数
     */
    public static String verCode() {
        Random random = new Random();
        return StringUtils.substring(String.valueOf(random.nextInt() * -10), 2, 6);
    }

    /**
     * 生成随机数
     *
     * @return 返回生成的随机数
     */
    public static String randomNumber() {
        Random random = new Random();
        return String.valueOf(Math.abs(random.nextInt() * -10));
    }
}
