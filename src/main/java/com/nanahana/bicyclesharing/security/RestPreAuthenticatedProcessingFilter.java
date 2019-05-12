package com.nanahana.bicyclesharing.security;

import com.nanahana.bicyclesharing.cache.CommonCacheUtils;
import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.util.AntPathMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/9 20:06
 * @Description 自定义过滤器，不拦截校验token通过的用户
 */
@Slf4j
public class RestPreAuthenticatedProcessingFilter extends AbstractPreAuthenticatedProcessingFilter {

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private List<String> nonSecurityList;

    private final static String OPTIONS_METHOD = "OPTIONS";

    private CommonCacheUtils commonCacheUtils;

    RestPreAuthenticatedProcessingFilter(List<String> nonSecurityList, CommonCacheUtils commonCacheUtils) {
        this.nonSecurityList = nonSecurityList;
        this.commonCacheUtils = commonCacheUtils;
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest httpServletRequest) {
        GrantedAuthority[] grantedAuthorities = new GrantedAuthority[1];
        if (isNoneSecurity(httpServletRequest.getRequestURI()) || OPTIONS_METHOD.equals(httpServletRequest.getMethod())) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Constant.ROLE_NOAUTH_USER);
            grantedAuthorities[0] = grantedAuthority;
            return new RestAuthenticationToken(Arrays.asList(grantedAuthorities));
        }
        /* 校验版本，检查token */
        String version = httpServletRequest.getHeader(Constant.REQUEST_VERSION_KEY);
        String token = httpServletRequest.getHeader(Constant.REQUEST_TOKEN_KEY);
        if (version == null) {
            httpServletRequest.setAttribute(Constant.HEADER_ERROR, Constant.RESP_STATUS_BADREQUEST);
        }
        if (httpServletRequest.getHeader(Constant.HEADER_ERROR) == null) {
            try {
                if (!StringUtils.isBlank(token)) {
                    /* 授权登陆用户"BIKE_CLIENT"的角色，并允许其进行访问 */
                    UserElement userElement = commonCacheUtils.getUserByToken(token);
                    if (userElement != null) {
                        GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Constant.ROLE_BIKE_CLIENT);
                        grantedAuthorities[0] = grantedAuthority;
                        RestAuthenticationToken restAuthenticationToken =
                            new RestAuthenticationToken(Arrays.asList(grantedAuthorities));
                        restAuthenticationToken.setUserElement(userElement);
                        return restAuthenticationToken;
                    } else {
                        httpServletRequest.setAttribute(Constant.HEADER_ERROR, Constant.RESP_STATUS_NOAUTH);
                    }
                } else {
                    log.warn("Got no token from request header");
                    httpServletRequest.setAttribute(Constant.HEADER_ERROR, Constant.RESP_STATUS_NOAUTH);
                }
            } catch (Exception e) {
                log.error("Fail to authenticate user", e);
            }
        }
        if (httpServletRequest.getAttribute(Constant.HEADER_ERROR) != null) {
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(Constant.ROLE_NONE);
            grantedAuthorities[0] = grantedAuthority;
        }
        return new RestAuthenticationToken(Arrays.asList(grantedAuthorities));
    }

    /**
     * 判断请求是否拦截的url
     *
     * @param requestURI 请求的url
     * @return 返回boolean值
     */
    private boolean isNoneSecurity(String requestURI) {
        boolean result = false;
        if (this.nonSecurityList != null) {
            for (String pattern : this.nonSecurityList) {
                if (antPathMatcher.match(pattern, requestURI)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest httpServletRequest) {
        return null;
    }
}
