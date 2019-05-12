package com.nanahana.bicyclesharing.security;

import com.nanahana.bicyclesharing.common.constant.Constant;
import com.nanahana.bicyclesharing.common.exception.BadCredentialException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

/**
 * @Author nana
 * @Date 2019/5/9 20:11
 * @Description springboot security provider
 */
public class RestAuthenticationProvider implements AuthenticationProvider {
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof PreAuthenticatedAuthenticationToken) {
            PreAuthenticatedAuthenticationToken preAuth = (PreAuthenticatedAuthenticationToken) authentication;
            RestAuthenticationToken sysAuth = (RestAuthenticationToken) preAuth.getPrincipal();
            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
                GrantedAuthority gauth = sysAuth.getAuthorities().iterator().next();
                if (Constant.ROLE_BIKE_CLIENT.equals(gauth.getAuthority())) {
                    return sysAuth;
                } else if (Constant.ROLE_NOAUTH_USER.equals(gauth.getAuthority())) {
                    return sysAuth;
                }
            }
        }
//        else if (authentication instanceof RestAuthenticationToken) {
//            RestAuthenticationToken sysAuth = (RestAuthenticationToken) authentication;
//            if (sysAuth.getAuthorities() != null && sysAuth.getAuthorities().size() > 0) {
//                GrantedAuthority gauth = sysAuth.getAuthorities().iterator().next();
//                if ("BIKE_CLIENT".equals(gauth.getAuthority())) {
//                    return sysAuth;
//                } else if ("NOAUTH_USER".equals(gauth.getAuthority())) {
//                    return sysAuth;
//                }
//            }
//        }
        throw new BadCredentialException("unknown error");
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(aClass) || RestAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
