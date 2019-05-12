package com.nanahana.bicyclesharing.common.rest;

import com.nanahana.bicyclesharing.cache.CommonCacheUtils;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author nana
 * @Date 2019/5/9 18:43
 * @Description controller公共功能
 */
@Component
@Slf4j
public class BaseController {

    private CommonCacheUtils commonCacheUtils;

    private static final String UNKNOWN = "unknown";

    @Autowired
    public void setCommonCacheUtils(CommonCacheUtils commonCacheUtils) {
        this.commonCacheUtils = commonCacheUtils;
    }

    /**
     * 根据request获取当前用户信息
     *
     * @return 返回当前用户
     */
    protected UserElement getCurrentUser() {
        HttpServletRequest request =
            ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String token = request.getHeader(Constant.REQUEST_TOKEN_KEY);
        UserElement userElement;
        if (!StringUtils.isBlank(token)) {
            try {
                userElement = commonCacheUtils.getUserByToken(token);
                return userElement;
            } catch (Exception e) {
                log.error("fail to get user by taken", e);
                throw e;
            }
        }
        return null;
    }

    /**
     * 根据request获取当前用户ip地址
     *
     * @param request request请求
     * @return 返回ip
     */
    protected String getIpFromRequest(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }

}
