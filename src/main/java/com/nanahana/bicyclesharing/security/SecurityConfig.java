package com.nanahana.bicyclesharing.security;

import com.nanahana.bicyclesharing.cache.CommonCacheUtils;
import com.nanahana.bicyclesharing.common.constant.Parameters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * @Author nana
 * @Date 2019/5/9 19:54
 * @Description spring security启动配置
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final Parameters parameters;

    private final CommonCacheUtils commonCacheUtils;

    @Autowired
    public SecurityConfig(Parameters parameters, CommonCacheUtils commonCacheUtils) {
        this.parameters = parameters;
        this.commonCacheUtils = commonCacheUtils;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(parameters.getNoneSecurityPath().toArray(new String[0])).permitAll()
            .anyRequest().authenticated()
            .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().httpBasic().authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and().addFilter(getPreAuthenticatedProcessingFilter());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        /* 忽略options方法的请求 */
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(new RestAuthenticationProvider());
    }

    /**
     * 获取filter
     *
     * @return 返回filter
     * @throws Exception 异常
     */
    private RestPreAuthenticatedProcessingFilter getPreAuthenticatedProcessingFilter() throws Exception {
        RestPreAuthenticatedProcessingFilter filter =
            new RestPreAuthenticatedProcessingFilter(parameters.getNoneSecurityPath(),
                commonCacheUtils);
        filter.setAuthenticationManager(this.authenticationManagerBean());
        return filter;
    }
}
