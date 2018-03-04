package com.evy.jing.redis;

import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.SerializeUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class JedisManager {
    private JedisPool jedisPool;

    /**
     * 获取Jedis实例
     * @return  获取出错返回null
     */
    public Jedis getJedis() {
        Jedis jedis = null;
        try {
            //获取pool实例
            jedis = getJedisPool().getResource();
        } catch (JedisConnectionException e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } catch (Exception ex) {
            LoggerUtils.error(getClass(), ex, ex.getMessage());
        }
        return jedis;
    }

    /**
     * 获取指定键的值
     * @param db_index  数据库编号
     * @param key   键
     * @return  返回键存储的值，不存在键则返回null
     */
    public byte[] getValueByKey(int db_index, byte[] key) {
        Jedis jedis = null;
        byte[] result = null;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            result = jedis.get(key);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
        return result;
    }

    /**
     * 根据指定键删除
     * @param db_index  数据库编号
     * @param key   键
     */
    public void deleteByKey(int db_index, byte[] key) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            Long result = jedis.del(key);
            LoggerUtils.debug(getClass(), "deleteByKey: %s", result);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
    }

    /**
     * 保存键值
     * @param db_index  数据库编号
     * @param key   键
     * @param value 值
     * @param expireTime 设置过期时间，单位：s
     */
    public void saveValueByKey(int db_index, byte[] key, byte[] value, int expireTime) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            jedis.set(key, value);
            if (expireTime > 0) {
                jedis.expire(key, expireTime);
            }
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
    }

    /**
     * 更新键存储的值
     * @param db_index  数据库编号
     * @param key   键
     * @param value 更新的值
     * @return  返回键存储的旧值，不存在则返回null
     */
    public byte[] updateByKey(int db_index, byte[] key, byte[] value){
        Jedis jedis = null;
        byte[] result = null;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            result = getValueByKey(db_index, key);
            long expireTime = 0;
            if (result != null){
                expireTime = jedis.ttl(key);
                deleteByKey(db_index, key);
            }
            LoggerUtils.debug(getClass(), "剩余生存时间:%s(s)", expireTime);
            saveValueByKey(db_index, key, value, Math.toIntExact(expireTime));
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
        return result;
    }

    /**
     * 获取所有键值
     *
     * @param db_index  数据库编号
     * @param redis_session_all 自定义redis存储session标识符
     * @return 返回所有键存储的值集合
     */
    public Set<byte[]> selectAllSession(int db_index, byte[] redis_session_all) {
        Jedis jedis = null;
        Set<byte[]> sessionSet = new HashSet<byte[]>();
        try {
            jedis = getJedis();
            jedis.select(db_index);
            Set<byte[]> keys = jedis.keys(redis_session_all);
            for (byte[] s : keys) {
                sessionSet.add(jedis.get(s));
            }
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
        return sessionSet;
    }

    /**
     * 获取所有键
     *
     * @param db_index  数据库编号
     * @param redis_session_all 键名标识
     * @return 返回键集合，不存在则返回空集合
     */
    public Set<byte[]> selectAllKey(int db_index, byte[] redis_session_all) {
        Jedis jedis = null;
        Set<byte[]> keys = new HashSet<byte[]>();
        try {
            jedis = getJedis();
            jedis.select(db_index);
            keys = jedis.keys(redis_session_all);
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
        return keys;
    }

    /**
     * 获取所有键
     * @param db_index  数据库编号
     * @return  返回键集合，不存在则返回空集合
     */
    public Set<byte[]> selectAllKey(int db_index) {
        Jedis jedis = null;
        Set<byte[]> keys = new HashSet<byte[]>();
        try {
            jedis = getJedis();
            jedis.select(db_index);
            keys = jedis.keys(new byte[]{'*'});
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
        return keys;
    }

    /**
     * 获取数据库所有值集合
     * @param db_index  数据库编号
     * @return  数据库为空则返回空集合
     */
    public Set<byte[]> selectAllValues(int db_index, Set<byte[]> keys){
        Set<byte[]> values = new HashSet<byte[]>();
        if (getSize(db_index) > 0){
            keys.forEach(key -> values.add(getValueByKey(db_index, key)));
        }
        return values;
    }

    /**
     * 删除所有键值
     *
     * @param db_index  数据库编号
     * @param redis_session_all 键名标识
     */
    public void deleteAll(int db_index, byte[] redis_session_all) {
        Jedis jedis = null;
        Set<byte[]> sessionSet;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            sessionSet = jedis.keys(redis_session_all);
            if (sessionSet != null) {
                sessionSet.forEach(key -> deleteByKey(db_index, key));
            }
        } catch (Exception e) {
            LoggerUtils.error(getClass(), e, e.getMessage());
        } finally {
            close(jedis);
        }
    }

    /**
     * 清空数据库
     *
     * @param db_index  数据库编号
     */
    public void deleteAll(int db_index) {
        Jedis jedis = null;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            jedis.flushDB();
//            jedis.flushAll();
        } finally {
            if (jedis != null) {
                close(jedis);
            }
        }
    }

    /**
     * 返回存储个数
     * @param db_index  数据库编号
     * @return  返回整型数量，不存在键返回0
     */
    public int getSize(int db_index){
        Jedis jedis = null;
        int result = 0;
        try {
            jedis = getJedis();
            jedis.select(db_index);
            result = jedis.dbSize().intValue();
        } finally {
            if (jedis != null){
                close(jedis);
            }
        }
        return result;
    }

    /**
     * 关闭redis连接
     *
     * @param jedis 将关闭的数据库
     */
    private void close(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }

    public JedisPool getJedisPool() {
        return jedisPool;
    }

    public void setJedisPool(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }
}
