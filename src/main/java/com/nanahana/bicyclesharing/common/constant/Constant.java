package com.nanahana.bicyclesharing.common.constant;

/**
 * @Author nana
 * @Date 2019/5/9 12:02
 * @Description 常用常量
 */
public class Constant {
    /**
     * 正常状态
     */
    public static final int RESP_STATUS_OK = 200;
    /**
     * 当前请求无法被服务器理解（请求参数有误）
     */
    public static final int RESP_STATUS_BADREQUEST = 400;
    /**
     * 无授权
     */
    public static final int RESP_STATUS_NOAUTH = 401;
    /**
     * 检查app版本更新
     */
    public static final int CHECK_APP_VERSION = 408;
    /**
     * 服务器内部出错
     */
    public static final int RESP_STATUS_INTERNAL_ERROR = 500;
    /**
     * 请求head中用user-token作为用户token的键
     */
    public static final String REQUEST_TOKEN_KEY = "user-token";
    /**
     * app版本号
     */
    public static final String REQUEST_VERSION_KEY = "version";
    /**
     * 请求head出错
     */
    public static final String HEADER_ERROR = "header-error";
    /**
     * security role 单车用户
     */
    public static final String ROLE_BIKE_CLIENT = "BIKE_CLIENT";
    /**
     * security role 未登录授权用户
     */
    public static final String ROLE_NOAUTH_USER = "NOAUTH_USER";
    /**
     * security role 单车用户及未登录用户以外的角色
     */
    public static final String ROLE_NONE = "NONE";

    /**
     * SMS app key
     */
    public static final String SMS_APP_KEY = "b1a45ef1a823d32843fd5624da39a0df";
    /**
     * SMS请求的url
     */
    public static final String SMS_REST_URL = "http://v.juhe.cn/sms/send";
    /**
     * SMS模版id
     */
    public static final String SMS_VERCODE_TPLID = "157584";

    /**
     * 七牛 key
     */
    public static final String QINIU_ACCESS_KEY = "MDhxkiJ17vtLw5v0kgLzJ6FfbTA_7mnMwHnPXWGc";
    /**
     * 七牛 secret key
     */
    public static final String QINIU_SECRET_KEY = "hvOhqWb4O3D3ZSj-G-vGYaotIlkonpPEHOBRzlqB";
    /**
     * 七牛空间名
     */
    public static final String QINIU_HEAD_IMG_BUCKET_NAME = "bicycle-sharing";
    /**
     * 七牛请求的url
     */
    public static final String QINIU_HEAD_IMG_BUCKET_URL = "pre0ffxxe.bkt.clouddn.com";

    /**
     * 百度推送api key
     */
    public static final String BAIDU_YUN_PUSH_API_KEY = "XmG45PtRigTG8CxOpD6wGshe";
    /**
     * 百度推送secret key
     */
    public static final String BAIDU_YUN_PUSH_SECRET_KEY = "VhEoMfGDPenLTIvTjZbhFv2dQGpA1W9I";
    /**
     * 百度推送请求url
     */
    public static final String CHANNEL_REST_URL = "api.push.baidu.com";
}
