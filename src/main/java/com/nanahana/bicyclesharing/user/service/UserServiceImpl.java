package com.nanahana.bicyclesharing.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nanahana.bicyclesharing.cache.CommonCacheUtils;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.BadCacheException;
import com.nanahana.bicyclesharing.common.exception.BadDataException;
import com.nanahana.bicyclesharing.common.exception.BadTokenException;
import com.nanahana.bicyclesharing.common.utils.QiNiuFileUploadUtil;
import com.nanahana.bicyclesharing.common.utils.RandomNumberCode;
import com.nanahana.bicyclesharing.jms.SmsProcessor;
import com.nanahana.bicyclesharing.security.AesUtils;
import com.nanahana.bicyclesharing.security.Base64Utils;
import com.nanahana.bicyclesharing.security.MD5Utils;
import com.nanahana.bicyclesharing.security.RsaUtils;
import com.nanahana.bicyclesharing.user.dao.UserMapper;
import com.nanahana.bicyclesharing.user.entity.User;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.jms.Destination;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author nana
 * @Date 2019/5/8 17:06
 * @Description 用户具体实现类
 */
@Service("userServiceImpl")
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     * 验证码前缀
     */
    private static final String VERIFY_CODE_PREFIX = "verify.code.";
    /**
     * 当前验证码未过期
     */
    private static final int RESULT_1 = 1;
    /**
     * 手机号超过当日验证码次数上限
     */
    private static final int RESULT_2 = 2;
    /**
     * ip超过当日验证码次数上线
     */
    private static final int RESULT_3 = 3;
    /**
     * 需要监听的队列名
     */
    private static final String SMS_QUEUE = "sms.queue";

    private final UserMapper userMapper;
    private final CommonCacheUtils commonCacheUtils;
    private final SmsProcessor smsProcessor;

    @Autowired
    public UserServiceImpl(UserMapper userMapper, CommonCacheUtils commonCacheUtils, SmsProcessor smsProcessor) {
        this.userMapper = userMapper;
        this.commonCacheUtils = commonCacheUtils;
        this.smsProcessor = smsProcessor;
    }

    @Override
    public String login(String data, String key) throws BadDataException {
        String token;
        String decryptData;
        try {
            /* 解析校验数据是否正确。 */
            byte[] aesKey = RsaUtils.decryptByPrivateKey(Base64Utils.decode(key));
            decryptData = AesUtils.decrypt(data, new String(aesKey, StandardCharsets.UTF_8));
            if (decryptData == null) {
                throw new Exception();
            }
            JSONObject jsonObject = JSON.parseObject(data);
            String mobile = jsonObject.getString("mobile");
            String code = jsonObject.getString("code");
            String platform = jsonObject.getString("platform");
            if (StringUtils.isBlank(mobile) || StringUtils.isBlank(code)) {
                throw new Exception();
            }
            /* 获取的redis验证码，用来比较验证码是否正确。正确则校验数据库中是否存在此号码。如不存在为用户注册并存入数据库。 */
            String verCode = commonCacheUtils.getCacheValue(mobile);
            User user;
            if (code.equals(verCode)) {
                user = userMapper.selectByMobile(mobile);
                if (user == null) {
                    user = new User();
                    user.setMobile(mobile);
                    user.setNickname(mobile);
                    userMapper.insertSelective(user);
                }
            } else {
                throw new BadDataException("手机号验证码不匹配");
            }
            /* 生成并token */
            try {
                token = generateToken(user);
            } catch (Exception e) {
                throw new BadTokenException("生成Token失败");
            }
            UserElement userElement = new UserElement();
            userElement.setUserId(user.getId());
            userElement.setMobile(mobile);
            userElement.setToken(token);
            userElement.setPlatform(platform);
            commonCacheUtils.putToken(userElement);
        } catch (Exception e) {
            log.error("解密失败", e);
            throw new BadDataException("数据解析错误。");
        }
        return null;
    }

    @Override
    public void modifyNickname(User user) {
        userMapper.updateByPrimaryKeySelective(user);
    }

    @Override
    public void sendVercode(String mobile, String ip) throws BadCacheException {
        String verCode = RandomNumberCode.verCode();
        /* 将验证码存入redis，再通过redis缓存检查是否恶意请求后决定是否真的发送验证码 */
        int result = commonCacheUtils.cacheForVerificationCode(VERIFY_CODE_PREFIX + mobile, verCode, "reg", 60, ip);
        if (result == RESULT_1) {
            log.info("当前验证码未过期，请稍后重试");
            throw new BadCacheException("当前验证码未过期，请稍后重试");
        } else if (result == RESULT_2) {
            log.info("超过当日验证码次数上线");
            throw new BadCacheException("超过当日验证码次数上限");
        } else if (result == RESULT_3) {
            log.info("超过当日验证码次数上限 {}", ip);
            throw new BadCacheException(ip + "超过当日验证码次数上限");
        }
        log.info("Sending verify code {} for phone {}", verCode, mobile);
        /* 将验证码推送到队列 */
        Destination destination = new ActiveMQQueue(SMS_QUEUE);
        Map<String, String> smsParam = new HashMap<>(3);
        smsParam.put("mobile", mobile);
        smsParam.put("tplId", Constant.SMS_VERCODE_TPLID);
        smsParam.put("vercode", verCode);
        String message = JSON.toJSONString(smsParam);
        smsProcessor.sendSmsToQueue(destination, message);
    }

    @Override
    public String uploadHeadImg(MultipartFile file, Long userId) throws BadDataException {
        try {
            /* 获取user得到原来的头像地址，调用七牛更新用户头像URL */
            User user = userMapper.selectByPrimaryKey(userId);
            String imgUrlName = QiNiuFileUploadUtil.uploadHeadImg(file);
            user.setHeadImg(imgUrlName);
            userMapper.updateByPrimaryKeySelective(user);
            return Constant.QINIU_HEAD_IMG_BUCKET_URL + "/" + Constant.QINIU_HEAD_IMG_BUCKET_NAME + "/" + imgUrlName;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new BadDataException("头像上传失败");
        }
    }

    /**
     * 用MD5生成token
     *
     * @param user 用户类
     * @return 返回token
     */
    private String generateToken(User user) {
        String source = user.getId() + "|" + user.getMobile() + "|" + System.currentTimeMillis();
        return MD5Utils.getMD5(source);
    }
}
