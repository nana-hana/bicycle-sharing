package com.nanahana.bicyclesharing.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.nanahana.bicyclesharing.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

/**
 * @Author nana
 * @Date 2019/5/11 19:42
 * @Description 短信消费者
 */
@Component(value = "smsProcessor")
public class SmsProcessor {

    private final JmsMessagingTemplate jmsMessagingTemplate;
    private final SmsSender smsSender;

    @Autowired
    public SmsProcessor(JmsMessagingTemplate jmsMessagingTemplate, @Qualifier("verCodeService") SmsSender smsSender) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
        this.smsSender = smsSender;
    }

    /**
     * 把消息发送到目的队列
     *
     * @param destination 消息去往的目的地队列
     * @param message     短信信息
     */
    public void sendSmsToQueue(Destination destination, final String message) {
        jmsMessagingTemplate.convertAndSend(destination, message);
    }

    /**
     * 监听队列“sms.queue”，执行发短信的指令
     *
     * @param text 需要发送的消息
     */
    @JmsListener(destination = "sms.queue")
    public void doSendSmsMessage(String text) {
        JSONObject jsonObject = JSON.parseObject(text);
        smsSender.sendSms(jsonObject.getString("mobile"), jsonObject.getString("tplId"), jsonObject.getString(
            "vercode"));
    }

}
