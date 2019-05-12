package com.nanahana.bicyclesharing.cache;

import com.nanahana.bicyclesharing.common.exception.BadCacheException;
import com.nanahana.bicyclesharing.user.entity.UserElement;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.Map;

/**
 * @Author nana
 * @Date 2019/5/9 16:04
 * @Description cache工具类
 */
@Component
@Slf4j
public class CommonCacheUtils {
    /**
     * token前缀
     */
    private static final String TOKEN_PREFIX = "token.";

    /**
     * user前缀
     */
    private static final String USER_PREFIX = "user.";

    /**
     * token超时时间
     */
    private static final int TIMEOUT = 2592000;

    private final JedisPoolWrapper jedisPoolWrapper;

    @Autowired
    public CommonCacheUtils(JedisPoolWrapper jedisPoolWrapper) {
        this.jedisPoolWrapper = jedisPoolWrapper;
    }

    /**
     * 永久缓存键值对
     *
     * @param key   键
     * @param value 值
     */
    public void cache(String key, String value) {
        try {
            @Cleanup JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                jedis.set(key, value);
            }
        } catch (Exception e) {
            log.error("Fail to cache value", e);
        }
    }

    /**
     * 获取缓存key
     *
     * @param key 键
     * @return 根据key获取缓存的value
     */
    public String getCacheValue(String key) {
        String value = null;
        try {
            @Cleanup JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                value = jedis.get(key);
            }
        } catch (Exception e) {
            log.error("Fail to get cached value", e);
        }
        return value;
    }

    /**
     * 期限缓存键值对
     *
     * @param key    键
     * @param value  值
     * @param expiry 过期日期
     * @return 返回操作成功失败（0失败）
     */
    public long cacheNxExpire(String key, String value, int expiry) {
        long result = 0;
        try {
            @Cleanup JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                result = jedis.setnx(key, value);
                jedis.expire(key, expiry);
            }
        } catch (Exception e) {
            log.error("Fail to cacheNx value", e);
        }
        return result;
    }

    /**
     * 根据key删除缓存的键值对
     *
     * @param key 键
     */
    public void delKey(String key) {
        try {
            @Cleanup JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                jedis.del(key);
            }
        } catch (Exception e) {
            log.error("Fail to remove key from redis", e);
        }
    }

    /**
     * 登录时缓存token
     *
     * @param userElement 用户基本信息
     */
    public void putToken(UserElement userElement) {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                Transaction trans = jedis.multi();
                try {
                    trans.del(TOKEN_PREFIX + userElement.getToken());
                    trans.hmset(TOKEN_PREFIX + userElement.getToken(), userElement.toMap());
                    trans.expire(TOKEN_PREFIX + userElement.getToken(), TIMEOUT);
                    trans.sadd(USER_PREFIX + userElement.getUserId(), userElement.getToken());
                    trans.exec();
                } catch (Exception e) {
                    trans.discard();
                    log.error("Fail to cache token to redis", e);
                }
            }
        } catch (Exception e) {
            log.error("Fail to cache token to redis", e);
        }
    }

    /**
     * 根据token取缓存的用户信息
     *
     * @param token 用户token
     * @return 返回用户信息
     */
    public UserElement getUserByToken(String token) {
        UserElement userElement = null;
        try {
            @Cleanup JedisPool pool = jedisPoolWrapper.getJedisPool();
            if (pool != null) {
                @Cleanup Jedis jedis = pool.getResource();
                jedis.select(0);
                Map<String, String> map = jedis.hgetAll(TOKEN_PREFIX + token);
                if (map != null && !map.isEmpty()) {
                    userElement = UserElement.toObject(map);
                } else {
                    log.warn("Fail to find cached element for token {}", token);
                }
            }
        } catch (Exception e) {
            log.error("Fail to get token from redis", e);
            throw e;
        }
        return userElement;
    }

    /**
     * 缓存手机验证码专用 限制了发送次数
     *
     * @param key     键：前缀（verify.code.）+手机号
     * @param value   值：验证码
     * @param type    验证码类型：reg
     * @param timeout 超时时长
     * @param ip      ip
     * @return 1 当前验证码未过期   2 手机号超过当日验证码次数上限  3 ip超过当日验证码次数上线
     * @throws BadCacheException 缓存异常
     */
    public int cacheForVerificationCode(String key, String value, String type, int timeout, String ip) throws BadCacheException {
        try {
            JedisPool pool = jedisPoolWrapper.getJedisPool();
            int vercodeTime = 10;
            if (pool != null) {
                try (Jedis jedis = pool.getResource()) {
                    jedis.select(0);
                    String ipKey = "ip." + ip;
                    if (ip == null) {
                        return 3;
                    } else {
                        String ipSendCount = jedis.get(ipKey);
                        try {
                            if (ipSendCount != null && Integer.parseInt(ipSendCount) >= vercodeTime) {
                                return 3;
                            }
                        } catch (NumberFormatException e) {
                            log.error("Fail to process ip send count", e);
                            return 3;
                        }
                    }
                    long success = jedis.setnx(key, value);
                    if (success == 0) {
                        return 1;
                    }
                    String sendCount = jedis.get(key + "." + type);
                    try {
                        if (sendCount != null && Integer.parseInt(sendCount) >= vercodeTime) {
                            jedis.del(key);
                            return 2;
                        }
                    } catch (NumberFormatException e) {
                        log.error("Fail to process send count", e);
                        jedis.del(key);
                        return 2;
                    }
                    try {
                        jedis.expire(key, timeout);
                        long val = jedis.incr(key + "." + type);
                        if (val == 1) {
                            jedis.expire(key + "." + type, 86400);
                        }
                        jedis.incr(ipKey);
                        if (val == 1) {
                            jedis.expire(ipKey, 86400);
                        }
                    } catch (Exception e) {
                        log.error("Fail to cache data into redis", e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Fail to cache for expiry", e);
            throw new BadCacheException("Fail to cache for expiry");
        }
        return 0;
    }
}
