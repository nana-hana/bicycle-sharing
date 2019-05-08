package com.nanahana.bicyclesharing.security;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * @Author nana
 * @Date 2019/5/8 19:59
 * @Description AES对称加密
 */
@Slf4j
public class AesUtil {
    /**
     * 产生密钥的算法
     */
    private static final String KEY_ALGORITHM = "AES";
    /**
     * 加解密算法 格式：算法/工作模式/填充模式
     */
    private static final String KEY_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";

    /**
     * AES对称加密
     *
     * @param data 数据
     * @param key  key需要16位
     * @return 无
     */
    public static String encrypt(String data, String key) {
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bs = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64Util.encode(bs);
        } catch (Exception e) {
            log.error("转码异常", e);
        }
        return null;
    }

    /**
     * AES对称解密
     *
     * @param data 数据
     * @param key  key需要16位
     * @return 无
     */
    public static String decrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE, spec, new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] originBytes = Base64Util.decode(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result, StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}