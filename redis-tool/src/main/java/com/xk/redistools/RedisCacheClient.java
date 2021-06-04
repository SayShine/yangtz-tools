package com.xk.redistools;

import com.xk.redistools.cache.CacheClient;
import com.xk.redistools.cache.CacheLoader;
import com.xk.redistools.cache.Null;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.TimeoutUtils;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.util.concurrent.TimeUnit;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-26 10:15
 * redis缓存操作客户端
 */
@Slf4j
public class RedisCacheClient implements CacheClient {

    private final RedisTemplate redisTemplate;

    private final RedisSerializer<String> keyRedisSerializer = new StringRedisSerializer();

    private final RedisSerializer<Object> valueRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

    public RedisCacheClient(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public <T> T getWithCacheLoader(String key, int exp, TimeUnit timeUnit, CacheLoader<T> cacheLoader) {
        return getWithCacheLoader(key, exp, timeUnit, false, cacheLoader);
    }

    @Override
    public  <T> T getWithCacheLoader(String key, int exp, TimeUnit timeUnit, boolean isCacheNull, CacheLoader<T> cacheLoader) {
        T value = get(key);
        // 缓存未命中
        if (value == null) {
            value = cacheLoader.load();
            if (isCacheNull) {
                setWithNull(key, value, exp, timeUnit);
            } else if (value != null) {
                set(key, value, exp, timeUnit);
            }
        }

        // 缓存获取到的Null对象
        if (value instanceof Null) {
            value = null;
        }
        return value;
    }

    private void setWithNull(String key, Object value, int exp, TimeUnit timeUnit) {
        if (value == null) {
            value = Null.NULL;
        }
        set(key, value, exp, timeUnit);
    }

    @Override
    public <T> T get(String key) {
        try {
            return (T) redisTemplate.execute((RedisCallback<T>) collection -> {
                byte[] valueByte = collection.get(keyRedisSerializer.serialize(key));
                return (T) valueRedisSerializer.deserialize(valueByte);
            });
        } catch (Exception e) {
            log.error("", e);
            return null;
        }

    }

    @Override
    public boolean set(String key, Object value, int exp, TimeUnit timeUnit) {
        if (value == null) {
            return false;
        }
        try {
            byte[] keyByte = keyRedisSerializer.serialize(key);
            byte[] valueByte = valueRedisSerializer.serialize(value);
            redisTemplate.execute(new RedisCallback<Object>() {

                @Override
                public Object doInRedis(RedisConnection connection) {
                    potentiallyUsePsetEx(connection);
                    return null;
                }

                public void potentiallyUsePsetEx(RedisConnection connection) {

                    if (!TimeUnit.MILLISECONDS.equals(timeUnit) || !failsafeInvokePsetEx(connection)) {
                        connection.setEx(keyByte, TimeoutUtils.toSeconds(exp, timeUnit), valueByte);
                    }
                }

                private boolean failsafeInvokePsetEx(RedisConnection connection) {

                    boolean failed = false;
                    try {
                        connection.pSetEx(keyByte, exp, valueByte);
                    } catch (UnsupportedOperationException e) {
                        // in case the connection does not support pSetEx return false to allow fallback to other operation.
                        failed = true;
                    }
                    return !failed;
                }
            }, true);
        } catch (Exception e) {
            log.error("cache set failed: key={},value={}", key, value, e);
            return false;
        }

        return true;
    }


}
