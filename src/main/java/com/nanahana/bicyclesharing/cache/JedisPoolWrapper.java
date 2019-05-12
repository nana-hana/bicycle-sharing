package com.nanahana.bicyclesharing.cache;

import com.nanahana.bicyclesharing.common.constant.Parameters;
import com.nanahana.bicyclesharing.common.exception.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.PostConstruct;

/**
 * @Author nana
 * @Date 2019/5/9 14:44
 * @Description redis连接池
 */
@Component
@Slf4j
public class JedisPoolWrapper {

    private JedisPool jedisPool;

    private final Parameters parameters;

    /**
     * redis链接等待超时时长
     */
    private static final int TIMEOUT = 2000;

    @Autowired
    public JedisPoolWrapper(Parameters parameters) {
        this.parameters = parameters;
    }

    @PostConstruct
    public void init() throws ConnectionException {
        try {
            JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
            jedisPoolConfig.setMaxWaitMillis(parameters.getRedisMaxWaitMillis());
            jedisPoolConfig.setMaxIdle(parameters.getRedisMaxIdle());
            jedisPoolConfig.setMaxTotal(parameters.getRedisMaxTotal());
            jedisPool = new JedisPool(jedisPoolConfig, parameters.getRedisHost(), parameters.getRedisPort(), TIMEOUT);
        } catch (Exception e) {
            log.error("初始化失败", e);
            throw new ConnectionException("初始化Redis失败。");
        }
    }

    JedisPool getJedisPool() {
        return jedisPool;
    }
}
