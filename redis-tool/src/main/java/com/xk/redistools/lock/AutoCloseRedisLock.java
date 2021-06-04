package com.xk.redistools.lock;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-29 19:34
 */
public class AutoCloseRedisLock implements RedisLock {

    private StringRedisTemplate stringRedisTemplate;

    private String key;

    private String expectedValue;

    private long threadId;

    private static final String COMPARE_AND_DELETE =
            "if redis.call('get',KEYS[1]) == ARGV[1]\n" +
                    "then\n" +
                    "    return redis.call('del',KEYS[1])\n" +
                    "else\n" +
                    "    return 0\n" +
                    "end";

    public AutoCloseRedisLock(StringRedisTemplate redisTemplate, String key, String expectedValue) {
        this.stringRedisTemplate = redisTemplate;
        this.key = key;
        this.expectedValue = expectedValue;
        this.threadId = Thread.currentThread().getId();
    }

    @Override
    public void close() throws Exception {
        if (threadId != Thread.currentThread().getId()) {
            throw new IllegalAccessException("threadId错误，无法释放锁");
        }
        List<String> keys = new ArrayList<>();
        keys.add(key);
        stringRedisTemplate.execute(new DefaultRedisScript<>(COMPARE_AND_DELETE, Long.class), keys, expectedValue);
    }

}
