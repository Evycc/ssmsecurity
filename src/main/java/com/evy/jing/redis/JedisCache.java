package com.evy.jing.redis;

import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.SerializeUtils;

import java.util.Collection;
import java.util.Set;

public class JedisCache<K, V> implements MyCache<K, V> {
    /**
     * 指定缓存数据库编号
     */
    private int db_index = 1;
    /**
     * 缓存名称
     */
    private String name;
    private JedisManager jedisManager;
    /**
     * 缓存时间  单位:s
     */
    private int timeOut = 0;

    @Override
    public V get(K k) {
        byte[] value = jedisManager.getValueByKey(db_index, SerializeUtils.ofSerialize(k));
        return (V) SerializeUtils.ofDeserialize(value);
    }

    @Override
    public V put(K k, V v) {
        V value = get(k);
        try {
            jedisManager.saveValueByKey(db_index, SerializeUtils.ofSerialize(k),
                    SerializeUtils.ofSerialize(v), timeOut);
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "put Error : %s", e.getMessage());
        }
        return value;
    }

    @Override
    public V remove(K k) {
        V value = get(k);
        try {
            jedisManager.deleteByKey(db_index, SerializeUtils.ofSerialize(k));
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "remove Error : %s", e.getMessage());
        }
        return value;
    }

    @Override
    public V update(K k, V v) {
        byte[] value = null;
        try {
            value = jedisManager.updateByKey(db_index, SerializeUtils.ofSerialize(k),
                    SerializeUtils.ofSerialize(v));
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "update Error : %s", e.getMessage());
        }
        return (V) value;
    }

    @Override
    public void clear() {
        try {
            jedisManager.deleteByKey(db_index, buildRedisSessionAll());
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "clear Error : %s", e.getMessage());
        }
    }

    @Override
    public int size() {
        Set set = keys();
        if (set == null) {
            return 0;
        }
        return set.size();
    }

    @Override
    public Set<K> keys() {
        Set<byte[]> set = null;
        try {
            set = jedisManager.selectAllKey(db_index);
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "keys Error : %s", e.getMessage());
        }
        return (Set<K>) set;
    }

    @Override
    public Collection<V> values() {
        Set<byte[]> set = null;
        try {
            set = jedisManager.selectAllValues(db_index, (Set<byte[]>) keys());
        } catch (Exception e) {
            LoggerUtils.errorStr(getClass(), e, "values Error : %s", e.getMessage());
        }
        return (Collection<V>) set;
    }

    /**
     * 自定义存储键名
     *
     * @param key
     * @return
     */
    private String buildCacheKey(Object key) {
        return getName() + ":" + key;
    }

    /**
     * 自定义获取所有键的键名
     *
     * @return
     */
    private byte[] buildRedisSessionAll() {
        return SerializeUtils.ofSerialize(getName() + "*");
    }

    /**
     * START get set
     **/

    public int getDb_index() {
        return db_index;
    }

    public void setDb_index(int db_index) {
        this.db_index = db_index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JedisManager getJedisManager() {
        return jedisManager;
    }

    public void setJedisManager(JedisManager jedisManager) {
        this.jedisManager = jedisManager;
    }

    public int getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }
    /**END get set**/
}
