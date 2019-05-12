package com.nanahana.bicyclesharing.common.response;

import com.nanahana.bicyclesharing.common.constant.Constant;
import lombok.Data;

/**
 * @Author nana
 * @Date 2019/5/9 11:13
 * @Description 请求状态结果返回
 */
@Data
public class ApiResult<T> {
    /**
     * 状态码（默认OK）
     */
    private int code = Constant.RESP_STATUS_OK;
    /**
     * 状态提示信息
     */
    private String message;
    /**
     * 取得的数据
     */
    private T data;
}
