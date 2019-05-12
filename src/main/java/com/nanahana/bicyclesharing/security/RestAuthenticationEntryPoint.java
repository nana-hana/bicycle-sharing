package com.nanahana.bicyclesharing.security;

import com.alibaba.fastjson.JSON;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.response.ApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Author nana
 * @Date 2019/5/9 20:15
 * @Description springboot security entry
 */
@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                         AuthenticationException e) throws IOException, ServletException {
        ApiResult result = new ApiResult();
        /* 检查请求head错误 */
        if (httpServletRequest.getAttribute(Constant.HEADER_ERROR) != null) {
            if (String.valueOf(Constant.RESP_STATUS_BADREQUEST).equals(httpServletRequest.getAttribute(Constant.HEADER_ERROR))) {
                result.setCode(Constant.CHECK_APP_VERSION);
                result.setMessage("请升级至app最新版本");
            } else {
                result.setCode(Constant.RESP_STATUS_NOAUTH);
                result.setMessage("请您登录");
            }
        }
        try {
            /* 设置跨域请求并将请求结果json刷到响应里 */
            httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
            httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, HEADER");
            httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
            httpServletResponse.setHeader("Access-Control-Allow-Headers", "X-Requested-With, user-token, " +
                "Content-Type, Accept, " +
                "version, type, platform");
            httpServletResponse.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
            httpServletResponse.getWriter().write(JSON.toJSONString(result));
            httpServletResponse.flushBuffer();
        } catch (Exception er) {
            log.error("Fail to send 401 response {}", er.getMessage());
        }
    }
}
