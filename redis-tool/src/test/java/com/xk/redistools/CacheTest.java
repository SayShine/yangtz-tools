package com.xk.redistools;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-26 10:51
 */
@Slf4j
public class CacheTest extends RedisToolsApplicationTests {

    @Resource(name = "redisTemplate")
    RedisCacheClient redisCacheClient;

    @Test
    public void testRedisCache() {
        for (int i = 0; i < 10; i++) {
            String str = redisCacheClient.getWithCacheLoader(RedisKeyFactory.TEST_KEY.join("test"), 1, TimeUnit.MINUTES, this::mockDb);
            System.out.println(str+" "+System.currentTimeMillis());
        }
    }

    private String mockDb() {
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "333";
    }
}
