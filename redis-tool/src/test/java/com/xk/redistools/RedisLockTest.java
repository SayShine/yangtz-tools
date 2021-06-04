package com.xk.redistools;

import com.xk.redistools.lock.RedisLock;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-29 19:59
 */
@Slf4j
public class RedisLockTest extends RedisToolsApplicationTests {

    @Resource(name = "redisTemplate")
    RedisAtomicClient redisAtomicClient;

    @Test
    public void testRedisLock() throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10,10,1000, TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000));
        for (int i = 0; i < 10000; i++) {
            executorService.execute(this::sayHello);
        }
        Thread.sleep(10000);
    }

    private void sayHello(){
        try(RedisLock redisLock = redisAtomicClient.getLock(RedisKeyFactory.TEST_KEY.join("lock"), 2)){
            if(redisLock == null){
                log.warn("获取锁失败");
            }
        } catch (Exception e) {
            log.error("业务失败");
        }
    }
}
