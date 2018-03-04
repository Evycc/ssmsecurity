package com.evy.jing.redis;

import com.evy.jing.model.ExcludeCacheModel;
import com.evy.jing.util.FactoryStaticResource;
import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.SerializeUtils;
import org.apache.ibatis.cache.Cache;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyBatis二级缓存redis实现
 *
 * @author : Evy
 * @date : Created 2018/1/23 14:04
 */
public class MyBatisCache implements Cache {
    /**
     * 指定缓存数据库编号
     */
    private final int db_index = 2;
    /**
     * 缓存时间  单位:s
     */
    private int timeOut = 0;
    private static JedisManager jedisManager;
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final String id;
    /**
     * 控制是否进行缓存，默认为false进行缓存
     */
    private boolean isExclude = false;

    public MyBatisCache(String id) {
        if (id == null) {
            LoggerUtils.error(MyBatisCache.class,
                    new IllegalArgumentException(), "MyBatisCache ID为空");
        }
        LoggerUtils.debug(MyBatisCache.class, "MyBatisCache id:%s", id);
        this.id = id;

        LoggerUtils.debug(MyBatisCache.class, "jedisManager : %s", jedisManager);
        getJedisManagerResource();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void putObject(Object key, Object value) {
        LoggerUtils.debug(MyBatisCache.class, "put key: %s, value: %s", key, value);
        //test
        ExcludeCacheModel model = FactoryStaticResource.getExcludeCacheModel();
        String s = key.toString();

        model.getName().forEach(name -> {
            if (Pattern.matches(buildExcludeString(name), s)) {
                LoggerUtils.debug(MyBatisCache.class, "不缓存该方法 : %s", s);
                setIsExcludeTrue();
            }
        });

        if (isExclude) {
            setIsExcludeFalse();
            return;
        }
        getJedisManagerResource();
        jedisManager.saveValueByKey(db_index, SerializeUtils.ofSerialize(key),
                SerializeUtils.ofSerialize(value), timeOut);
    }

    @Override
    public Object getObject(Object o) {
        Object result = null;
        getJedisManagerResource();
        result = SerializeUtils.ofDeserialize(jedisManager.getValueByKey(db_index,
                SerializeUtils.ofSerialize(o)));
        return result;
    }

    @Override
    public Object removeObject(Object o) {
        Object result = null;
        getJedisManagerResource();
        result = SerializeUtils.ofDeserialize(jedisManager.getValueByKey(db_index,
                SerializeUtils.ofSerialize(o)));
        jedisManager.deleteByKey(db_index, SerializeUtils.ofSerialize(o));
        return result;
    }

    @Override
    public void clear() {
        getJedisManagerResource();
        jedisManager.deleteAll(db_index);
    }

    @Override
    public int getSize() {
        getJedisManagerResource();
        return jedisManager.getSize(db_index);
    }

    @Override
    public ReadWriteLock getReadWriteLock() {
        return readWriteLock;
    }

    /**
     * 获取JedisManager实例
     */
    private void getJedisManagerResource() {
        if (jedisManager == null) {
            jedisManager = FactoryStaticResource.getJedisManager();
        }
    }

    /**
     * 构造匹配方法字符串
     *
     * @param name
     * @return
     */
    private String buildExcludeString(String name) {
        return "[\\S\\s]*" + name + "[\\S\\s]*";
    }

    /**
     * isExclude为true则不进行缓存操作
     */
    private void setIsExcludeTrue() {
        isExclude = true;
    }

    /**
     * isExclude为false则进行缓存操作
     */
    private void setIsExcludeFalse(){
        isExclude = false;
    }
}
