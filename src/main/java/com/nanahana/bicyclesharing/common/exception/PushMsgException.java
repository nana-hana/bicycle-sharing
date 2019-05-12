package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/12 18:56
 * @Description 消息推送异常
 */
public class PushMsgException extends Exception {
    public PushMsgException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
