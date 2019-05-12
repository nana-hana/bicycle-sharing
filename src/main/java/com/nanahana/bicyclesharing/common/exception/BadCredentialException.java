package com.nanahana.bicyclesharing.common.exception;

import com.nanahana.bicyclesharing.common.constant.Constant;
import org.springframework.security.core.AuthenticationException;

/**
 * @Author nana
 * @Date 2019/5/9 21:53
 * @Description 授权异常
 */
public class BadCredentialException extends AuthenticationException {
    public BadCredentialException(String msg) {
        super(msg);
    }

    public int getStatusCode() {
        return Constant.RESP_STATUS_INTERNAL_ERROR;
    }
}
