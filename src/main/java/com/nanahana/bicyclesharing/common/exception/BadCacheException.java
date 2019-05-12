package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/11 15:32
 * @Description 缓存异常
 */
public class BadCacheException extends Exception {

    public BadCacheException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
