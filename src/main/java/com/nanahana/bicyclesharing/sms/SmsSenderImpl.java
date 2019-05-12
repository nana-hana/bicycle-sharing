package com.nanahana.bicyclesharing.sms;

import com.alibaba.fastjson.JSONObject;
import com.nanahana.bicyclesharing.common.constant.Constant;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author nana
 * @Date 2019/5/11 15:48
 * @Description 验证码发送实现类
 */
@Service("verCodeService")
@Slf4j
public class SmsSenderImpl implements SmsSender {
    /**
     * 编码格式
     */
    private static final String DEF_CHARSET = "UTF-8";
    /**
     * 链接超时时长
     */
    private static final int DEF_CONN_TIMEOUT = 30000;
    /**
     * 读取超时时长
     */
    private static final int DEF_READ_TIMEOUT = 30000;
    /**
     * 验证码发送错误值
     */
    private static final String ERROR_CODE = "error_code";

    @Override
    public void sendSms(String phone, String tplId, String tplValue) {
        String prefixUrl = Constant.SMS_REST_URL;
        Map<String, Object> params = new HashMap<>(5);
        params.put("mobile", phone);
        params.put("tpl_id", tplId);
        params.put("tpl_value", tplValue);
        params.put("key", Constant.SMS_APP_KEY);
        params.put("dtype", "json");
        String rs = null;
        try {
            StringBuilder sb = new StringBuilder();
            URL url = new URL(prefixUrl + "?" + urlEncode(params));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setUseCaches(false);
            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
            conn.setReadTimeout(DEF_READ_TIMEOUT);
            conn.setInstanceFollowRedirects(false);
            conn.connect();
            @Cleanup InputStream is = conn.getInputStream();
            @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(is, DEF_CHARSET));
            String strRead;
            while ((strRead = reader.readLine()) != null) {
                sb.append(strRead);
            }
            rs = sb.toString();
            JSONObject object = JSONObject.parseObject(rs);
            if (object.getInteger(ERROR_CODE) == 0) {
                log.error(object.get("result").toString());
            } else {
                log.error(object.get("error_code") + ":" + object.get("reason"));
            }
        } catch (IOException e) {
            log.error("fail to send sms to " + phone + ":" + tplValue + ":" + rs);
            e.printStackTrace();
        }
    }

    /**
     * url转码
     *
     * @param data 需要转码的数据Map格式
     * @return 返回转码的数据String格式
     */
    private String urlEncode(Map<String, Object> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry i : data.entrySet()) {
            try {
                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
