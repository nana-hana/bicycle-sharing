package com.nanahana.bicyclesharing.common.utils;

import com.baidu.yun.push.auth.PushKeyPair;
import com.baidu.yun.push.client.BaiduPushClient;
import com.baidu.yun.push.exception.PushClientException;
import com.baidu.yun.push.exception.PushServerException;
import com.baidu.yun.push.model.PushMsgToSingleDeviceRequest;
import com.baidu.yun.push.model.PushMsgToSingleDeviceResponse;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.PushMsgException;
import com.nanahana.bicyclesharing.user.entity.UserElement;

/**
 * @Author nana
 * @Date 2019/5/12 18:40
 * @Description 百度推送
 */
public class BaiduPushUtil {

    /**
     * 消息有效市场
     */
    private static final String TIME = "3600";
    /**
     * 推送目标类型web
     */
    private static final int FOR_WEB = 1;
    /**
     * 推送目标类型pc
     */
    private static final int FOR_PC = 2;
    /**
     * 推送目标类型android
     */
    private static final int FOR_ANDROID = 3;
    /**
     * 推送目标类型ios
     */
    private static final int FOR_IOS = 4;
    /**
     * 推送目标类型wp
     */
    private static final int FOR_WP = 5;
    /**
     * 手机类型android
     */
    private static final String ANDROID = "android";
    /**
     * 手机类型ios
     */
    private static final String IOS = "ios";


    /**
     * 单个设备推送
     *
     * @param userElement 要推送的用户
     * @param message     推送的信息
     * @throws PushMsgException 推送消息异常
     */
    public static void pushMsgToSingleDevice(UserElement userElement, String message) throws PushMsgException {

        PushKeyPair pair = new PushKeyPair(Constant.BAIDU_YUN_PUSH_API_KEY, Constant.BAIDU_YUN_PUSH_SECRET_KEY);
        BaiduPushClient pushClient = new BaiduPushClient(pair, Constant.CHANNEL_REST_URL);
        try {
            PushMsgToSingleDeviceRequest request = new PushMsgToSingleDeviceRequest().
                addChannelId(userElement.getPushChannelId()).
                addMsgExpires(new Integer(TIME)).
                addMessageType(FOR_WEB).
                addMessage(message);
            if (ANDROID.equals(userElement.getPlatform())) {
                request.addDeviceType(FOR_ANDROID);
            } else if (IOS.equals(userElement.getPlatform())) {
                request.addDeviceType(FOR_IOS);
            }
            PushMsgToSingleDeviceResponse response = pushClient.pushMsgToSingleDevice(request);
        } catch (PushClientException e) {
            e.printStackTrace();
            throw new PushMsgException(e.getMessage());
        } catch (PushServerException e) {
            e.printStackTrace();
            throw new PushMsgException(e.getErrorMsg());
        }

    }
}
