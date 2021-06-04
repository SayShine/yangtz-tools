package com.xk.redistools.cache;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-26 10:13
 * 实际获取缓存的接口
 */
@FunctionalInterface
public interface CacheLoader<T> {

    /**
     * 实际获取数据的方法
     * @return 要加入缓存的数据
     */
     T load();
}
