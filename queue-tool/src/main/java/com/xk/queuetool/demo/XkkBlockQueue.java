package com.xk.queuetool.demo;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-06-16 14:54
 */
public class XkkBlockQueue<T> {

    private Object[] queue;

    private int putIndex = 0;

    private int count;

    private ReentrantLock lock;

    private Condition notEmpty;

    private Condition notFull;

    public XkkBlockQueue(int limit, boolean fair){
        if(limit <= 0){
            throw new IllegalArgumentException();
        }
        queue = new Object[limit];
        lock = new ReentrantLock(fair);
        notEmpty = lock.newCondition();
        notFull = lock.newCondition();
    }

    /**
     * 入队（队列满了的话，线程会等待，并重新入队）
     */
    public void put(T t) throws InterruptedException {
        if(t == null){
            throw new IllegalArgumentException();
        }
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try{
            while (count == queue.length){
                notFull.wait();
            }
            enqueue(t);
        }finally {
            lock.unlock();
        }
    }

    public boolean offer(T t) throws InterruptedException {
        checkNonNull(t);
        final ReentrantLock lock = this.lock;
        lock.lockInterruptibly();
        try{
            if(count == queue.length){
                return false;
            }
            enqueue(t);
            return true;
        }finally {
            lock.unlock();
        }
    }

    private void checkNonNull(T t) {
        if(t == null){
            throw new IllegalArgumentException();
        }
    }

    private void enqueue(T t) {
        final Object[] queue = this.queue;
        queue[putIndex] = t;
        if(++putIndex == queue.length){
            putIndex = 0;
        }
        count++;
        notEmpty.signal();
    }
}
