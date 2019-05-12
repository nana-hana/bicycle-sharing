package com.nanahana.bicyclesharing.common.constant;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author nana
 * @Date 2019/5/9 14:44
 * @Description 常用参数
 */
@Component
@Data
public class Parameters {
    /**
     * redis主机
     */
    @Value("${redis.host}")
    private String redisHost;
    /**
     * redis端口
     */
    @Value("${redis.port}")
    private int redisPort;
    /**
     * redis最大连接数
     */
    @Value("${redis.max-total}")
    private int redisMaxTotal;
    /**
     * redis最大空闲链接
     */
    @Value("${redis.max-idle}")
    private int redisMaxIdle;
    /**
     * redis最大阻塞等待时长
     */
    @Value("${redis.max-wait-millis}")
    private int redisMaxWaitMillis;
    /**
     * security无需拦截的url
     */
    @Value("#{'${security.noneSecurityPath}'.split(',')}")
    private List<String> noneSecurityPath;
}
