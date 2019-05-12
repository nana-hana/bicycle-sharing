package com.nanahana.bicyclesharing.common.utils;

import lombok.Cleanup;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Author nana
 * @Date 2019/5/11 15:05
 * @Description http client(post,get)
 */
@Slf4j
public class HttpUtil {

    /**
     * 编码
     */
    private static final String ENCODING = "UTF-8";

    /**
     * post请求
     *
     * @param url       请求地址
     * @param paramsMap 请求带的参数
     * @return 返回请求结果
     */
    public static String post(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        try {
            HttpPost method = new HttpPost(url);
            if (paramsMap != null) {
                List<NameValuePair> paramList = new ArrayList<>();
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    NameValuePair pair = new BasicNameValuePair(param.getKey(), param.getValue());
                    paramList.add(pair);
                }
                method.setEntity(new UrlEncodedFormEntity(paramList, ENCODING));
            }
            @Cleanup CloseableHttpResponse response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            log.error("http request failed", e);
        }
        return responseText;
    }

    /**
     * get请求
     *
     * @param url       请求地址
     * @param paramsMap 请求带的参数
     * @return 返回请求结果
     */
    public static String get(String url, Map<String, String> paramsMap) {
        CloseableHttpClient client = HttpClients.createDefault();
        String responseText = "";
        try {
            StringBuilder getUrl = new StringBuilder(url + "?");
            if (paramsMap != null) {
                for (Map.Entry<String, String> param : paramsMap.entrySet()) {
                    getUrl.append(param.getKey()).append("=").append(URLEncoder.encode(param.getValue(), ENCODING)).append("&");
                }
            }
            HttpGet method = new HttpGet(getUrl.toString());
            @NonNull CloseableHttpResponse response = client.execute(method);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                responseText = EntityUtils.toString(entity);
            }
        } catch (Exception e) {
            log.error("http request failed", e);
        }
        return responseText;
    }
}
