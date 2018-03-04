package com.evy.jing.util;

import org.springframework.util.SerializationUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 序列化对象工具类
 */
public class SerializeUtils {
    public static byte[] ofSerialize(Object obj){
        return SerializationUtils.serialize(obj);
    }
    public static Set<byte[]> ofSerialize(Collection<? extends Object> objects){
        Set<byte[]> set = new HashSet<byte[]>();
        for(Object obj : objects){
            byte[] newObj = SerializationUtils.serialize(obj);
            set.add(newObj);
        }
        return set;
    }
    public static Object ofDeserialize(byte[] obj){
        return SerializationUtils.deserialize(obj);
    }
    public static <T> T ofDeserialize(byte[] obj, Class<T> clazz){
        return (T) SerializationUtils.deserialize(obj);
    }
    public static <T> Set ofDeserialize(Collection<byte[]> objects, Class<T> clazz){
        Set<T> set = new HashSet<T>();
        for(byte[] obj : objects) {
            Object newObj = SerializationUtils.deserialize(obj);
            set.add((T) newObj);
        }
        return set;
    }
}
