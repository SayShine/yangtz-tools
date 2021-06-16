package com.xk.queuetool.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-06-16 14:08
 * 阻塞队列
 */
public class XkBlockQueue<T> {

    private static final Logger log = LoggerFactory.getLogger(XkBlockQueue.class);

    /**
     * 队列容量
     */
    private int limit = 10;

    /**
     * 内部队列
     */
    private final Queue<T> queue = new LinkedList<>();

    public XkBlockQueue(int limit){
        this.limit = limit;
    }

    /**
     * 入队
     */
    public synchronized boolean push(T t) throws InterruptedException {
        if(queue.size() >= limit){
            log.warn("queue reached max size! maybe the length is too small!");
            wait();
        }
        if(queue.size() == 0){
            notifyAll();
        }
        return this.queue.offer(t);
    }

    /**
     * 出队
     */
    public synchronized T pop() throws InterruptedException {
        if(queue.size() == 0){
            wait();
        }
        if(queue.size() >= limit){
            notifyAll();
        }
        return this.queue.poll();
    }

}
