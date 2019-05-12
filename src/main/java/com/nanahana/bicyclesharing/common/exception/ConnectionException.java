package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/9 15:14
 * @Description 连接失败异常
 */
public class ConnectionException extends Exception {

    public ConnectionException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
