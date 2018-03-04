package com.evy.jing.redis;

import java.util.Collection;
import java.util.Set;

/**
 * 自定义Cache
 * 定义常用Cache方法
 * @param <K>   键类型
 * @param <V>   值类型
 */
public interface MyCache<K,V> {
    /**
     * 根据键获取值
     * @param k 键
     * @return  不存在返回null
     */
    V get(K k);

    /**
     * 添加到缓存
     * @param k 键
     * @param v 值
     * @return  键已存在，则返回存储的值并且更新，不存在则返回null
     */
    V put(K k, V v);

    /**
     * 删除指定键的值
     * @param k 键
     * @return  返回键存储的值
     */
    V remove(K k);

    /**
     * 更新指定键
     * @param k 键
     * @param v 更新的值
     * @return  返回旧键存储的值，不存在则返回null
     */
    V  update(K k, V v);

    /**
     * 清空缓存
     */
    void clear();

    /**
     * 获取键数量
     * @return  返回整型数量，不存在键则返回0
     */
    int size();

    /**
     * 获取所有键
     * @return  返回键集合，不存在则返回空集合
     */
    Set<K> keys();

    /**
     * 获取所有值
     * @return  返回值集合，数据库为空返回空集合
     */
    Collection<V> values();

}
