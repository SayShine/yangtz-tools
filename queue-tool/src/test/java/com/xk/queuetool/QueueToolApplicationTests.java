package com.xk.queuetool;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@SpringBootTest
class QueueToolApplicationTests {

    ExecutorService executorService = Executors.newScheduledThreadPool(10);

    @Test
    void contextLoads() {
    }

}
