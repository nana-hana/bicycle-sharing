package com.nanahana.bicyclesharing.security;

import com.nanahana.bicyclesharing.user.entity.UserElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @Author nana
 * @Date 2019/5/9 21:12
 * @Description springboot security token
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RestAuthenticationToken extends AbstractAuthenticationToken {

    private UserElement userElement;

    RestAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }
}
