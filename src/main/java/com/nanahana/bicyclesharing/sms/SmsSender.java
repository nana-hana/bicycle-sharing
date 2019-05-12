package com.nanahana.bicyclesharing.sms;

/**
 * @Author nana
 * @Date 2019/5/11 15:47
 * @Description 验证码发送
 */
public interface SmsSender {
    /**
     * 通过聚合数据发送短信
     *
     * @param phone    手机
     * @param tplId    模版id
     * @param tplValue 验证码
     */
    void sendSms(String phone, String tplId, String tplValue);
}
