package com.xk.redistools;

import com.xk.redistools.lock.AutoCloseRedisLock;
import com.xk.redistools.lock.RedisLock;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-29 17:39
 */
@Component
public class RedisAtomicClient {

    private StringRedisTemplate stringRedisTemplate;

    /**
     * lua脚本：value自增同时设置失效时间
     */
    private static final String INCR_BY_WITH_TIMEOUT = "local v;" +
            " v = redis.call('incrBy',KEYS[1],ARGV[1]);" +
            "if tonumber(v) == tonumber(ARGV[1]) then\n" +
            "    redis.call('expire',KEYS[1],ARGV[2])\n" +
            "end\n" +
            "return v";


    public RedisAtomicClient(RedisTemplate redisTemplate) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
        stringRedisTemplate.setConnectionFactory(redisTemplate.getConnectionFactory());
        stringRedisTemplate.afterPropertiesSet();
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * redis原子类自增
     *
     * @param key 键
     * @param delta 自增数，可为负值
     * @param exp 过期时间
     * @param timeUnit 时间单位
     * @return 自增后的数
     */
    public Long incrBy(String key, long delta, long exp, TimeUnit timeUnit) {
        List<String> keys = new ArrayList<>();
        keys.add(key);
        long timeoutSeconds = TimeUnit.SECONDS.convert(exp, timeUnit);
        String[] args = new String[2];
        args[0] = String.valueOf(delta);
        args[1] = String.valueOf(timeoutSeconds);
        Long currentVal = stringRedisTemplate.execute(new DefaultRedisScript<>(INCR_BY_WITH_TIMEOUT, Long.class), keys, args);

        if (currentVal == null) {
            return null;
        }
        return currentVal;
    }

    /**
     * 获取redis锁的方法，获取不到则返回空
     *
     * @param key 锁的key值
     * @param exp 锁的失效时间（单位：秒）
     * @return redis锁
     */
    public RedisLock getLock(String key, long exp) {
        return getLock(key, exp, 0, 0);
    }

    /**
     * 重试会造成大量的线程挂起，请尽量避免使用此方法
     * 获取redis锁的方法，获取不到则返回空，可以设置最大重试次数与最大重试时间
     *
     * @param key                     缓存的key值
     * @param exp                     超时时间
     * @param maxRetryTime            最大重试次数
     * @param retryIntervalTimeMillis 重试前的等待时间
     * @return redis锁
     */
    public RedisLock getLock(final String key, final long exp, long maxRetryTime, long retryIntervalTimeMillis) {
        maxRetryTime += 1;
        final String value = UUID.randomUUID().toString();

        for (int i = 0; i < maxRetryTime; i++) {
            Boolean result = stringRedisTemplate.execute((RedisCallback<Boolean>) connection ->
                    connection.set(key.getBytes(), value.getBytes(), Expiration.seconds(exp), RedisStringCommands.SetOption.ifAbsent()));
            if (result != null && result) {
                return new AutoCloseRedisLock(stringRedisTemplate, key, value);
            }

            if (retryIntervalTimeMillis > 0) {
                try {
                    Thread.sleep(retryIntervalTimeMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            if (Thread.currentThread().isInterrupted()) {
                break;
            }
        }
        return null;
    }

}
