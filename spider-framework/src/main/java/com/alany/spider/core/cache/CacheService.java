package com.alany.spider.core.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by alany on 2019/07/24.
 */
@Service
public class CacheService<K, V> {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);

    private int cacheMaxSize = 3000;

    public void setCacheMaxSize(int cacheMaxSize) {
        this.cacheMaxSize = cacheMaxSize;
    }

    // 缓存
    private Cache<K, V> cache = CacheBuilder.newBuilder()
            .maximumSize(cacheMaxSize)
            .removalListener(new RemovalListener<K, V>() { //移出缓存时执行的操作
                @Override
                public void onRemoval(RemovalNotification<K, V> notification) {
                    if (notification.wasEvicted()) {
                        LOGGER.debug("key[" + notification.getKey() + "] was removed with expired.");
                    } else {
                        LOGGER.debug("key[" + notification.getKey() + "] was updated with put operation.");
                    }
                }
            })
            .build();


    public boolean containsKey(K key) {
        try {
            return cache.getIfPresent(key) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public long size() {
        return cache.size();
    }

    public V get(K key) {
        return cache.getIfPresent(key);
    }

    public ConcurrentMap<K, V> getAll() {
        return cache.asMap();
    }

    public List<V> getValues(){
        return new ArrayList(getAll().values());
    }

    public void put(K key, V value) {
        cache.put(key, value);
        LOGGER.debug(String.format("put key %s with value %s to cache...", key, JSON.toJSONString(value)));
    }

    public void remove(K key) {
        if (containsKey(key)) {
            cache.invalidate(key);
        }
    }
}
