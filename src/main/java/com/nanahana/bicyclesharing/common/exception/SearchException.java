package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;

/**
 * @Author nana
 * @Date 2019/5/12 12:18
 * @Description 查找异常
 */
public class SearchException extends Exception {
    public SearchException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
