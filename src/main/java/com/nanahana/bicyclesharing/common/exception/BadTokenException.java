package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/9 17:16
 * @Description token生成异常
 */
public class BadTokenException extends Exception {

    public BadTokenException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
