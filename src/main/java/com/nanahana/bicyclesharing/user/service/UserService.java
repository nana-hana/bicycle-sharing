package com.nanahana.bicyclesharing.user.service;

import com.nanahana.bicyclesharing.common.exception.BadCacheException;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.user.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * @Author nana
 * @Date 2019/5/8 17:05
 * @Description 用户Service接口类
 */
public interface UserService {

    /**
     * 用户登录
     *
     * @param data 登录信息密文
     * @param key  RSA加密的AES密匙（加密过的公钥）
     * @return 返回json格式的token
     * @throws BadDataException 数据解析异常
     */
    String login(String data, String key) throws BadDataException;

    /**
     * 修改用户昵称
     *
     * @param user 用户要修改的昵称
     */
    void modifyNickname(User user);

    /**
     * 给指定手机号码发送短信验证码
     *
     * @param mobile 手机号码
     * @param ip     ip（用于防止重复获取短信验证码）
     * @throws BadCacheException 缓存异常
     */
    void sendVercode(String mobile, String ip) throws BadCacheException;

    /**
     * 上传头像图片
     *
     * @param file   需要上传的头像图片文件
     * @param userId 上传的用户
     * @return 上传的图片链接
     * @throws BadDataException 异常
     */
    String uploadHeadImg(MultipartFile file, Long userId) throws BadDataException;
}
