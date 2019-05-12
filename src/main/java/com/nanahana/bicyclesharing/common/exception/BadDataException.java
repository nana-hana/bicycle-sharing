package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/9 13:10
 * @Description 数据异常（数据为空，不合法等等）
 */
public class BadDataException extends Exception {

    public BadDataException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_BADREQUEST;
    }
}
