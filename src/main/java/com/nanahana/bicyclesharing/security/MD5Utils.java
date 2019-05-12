package com.nanahana.bicyclesharing.security;

import org.apache.commons.codec.digest.DigestUtils;

/**
 * @Author nana
 * @Date 2019/5/9 17:10
 * @Description MD5工具类
 */
public class MD5Utils {

    /**
     * 加密字符串
     *
     * @param source 需要加密的资源
     * @return 返回加密之后的资源
     */
    public static String getMD5(String source) {
        return DigestUtils.md5Hex(source);
    }
}
