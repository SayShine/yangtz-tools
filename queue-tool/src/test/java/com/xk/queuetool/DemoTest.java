package com.xk.queuetool;

import com.xk.queuetool.common.StudentBean;
import com.xk.queuetool.demo.XkBlockQueue;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-06-16 14:22
 */
public class DemoTest extends QueueToolApplicationTests{

    private static final Logger log = LoggerFactory.getLogger(DemoTest.class);

    @Test
    public void xkBlockQueue() throws InterruptedException {
        XkBlockQueue<StudentBean> studentQueue = new XkBlockQueue<>(20);

        // 入队
        executorService.execute(() -> {
            try {
                push(studentQueue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        // 出队
        executorService.execute(() -> {
            try {
                pop(studentQueue);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        Thread.sleep(300000);
        System.out.println("阻塞住test");
    }

    private void push(XkBlockQueue<StudentBean> studentQueue) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            log.info("入队");
            studentQueue.push(new StudentBean(i));
        }
    }

    private void pop(XkBlockQueue<StudentBean> studentQueue) throws InterruptedException {
        for (int i = 0; i < 50; i++) {
            log.info("出队");
            // mock db
            Thread.sleep(500);
            System.out.println(studentQueue.pop());
        }
    }
}
