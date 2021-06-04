package com.xk.redistools.cache;

import java.util.concurrent.TimeUnit;

/**
 * @author xiongkai
 * @version 1.0
 * @date 2021-03-26 10:06
 */
public interface CacheClient {

    /**
     * 缓存命中直接返回，未命中则调用CacheLoader获取数据并加入到缓存中
     *
     * @param key         缓存的key值
     * @param exp         缓存失效时间
     * @param timeUnit    缓存失效单位
     * @param cacheLoader 实际获取数据的方法
     * @param <T>         返回对象类型
     * @return 缓存的value
     */
    <T> T getWithCacheLoader(String key, int exp, TimeUnit timeUnit, CacheLoader<T> cacheLoader);

    /**
     * 缓存命中直接返回，未命中则调用CacheLoader获取数据并加入到缓存中
     *
     * @param key         缓存的key值
     * @param exp         缓存失效时间
     * @param timeUnit    缓存失效单位
     * @param isCacheNull 是否允许缓存为空
     * @param cacheLoader 实际获取数据的方法
     * @param <T>         返回对象类型
     * @return 缓存的value
     */
    <T> T getWithCacheLoader(String key, int exp, TimeUnit timeUnit, boolean isCacheNull, CacheLoader<T> cacheLoader);

    /**
     * 缓存命中直接返回，未命中返回null
     *
     * @param key 缓存key值
     * @param <T> 返回对象类型
     * @return 缓存的value
     */
    <T> T get(String key);

    /**
     * 设置缓存
     * @param key 缓存的key值
     * @param value 缓存的value
     * @param exp 缓存失效时间
     * @param timeUnit 时间单位
     * @return 缓存是否设置成功
     */
    boolean set(String key, Object value, int exp, TimeUnit timeUnit);
}
