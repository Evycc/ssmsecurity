package com.evy.jing.pageplugin;

import com.evy.jing.util.LoggerUtils;

import java.lang.reflect.Field;

/**
 * 通过反射操作对象字段信息
 */
public class ReflectHelper {
    /**
     * 获取对象中的字段
     * @param object    包含字段的对象类
     * @param fieldName 字段名称
     * @return  获取不到返回null
     */
    public static Field getFieldByFieldName(Object object, String fieldName){
        for (Class superClass = object.getClass(); superClass != Object.class;
                superClass = superClass.getSuperclass()){
            try {
                return superClass.getDeclaredField(fieldName);
            } catch (NoSuchFieldException e) {
//                LoggerUtils.errorStr(ReflectHelper.class, "获取字段:%s 出错", fieldName);
            }
        }
        return null;
    }

    /**
     * 获取对象中的字段的值
     * @param object    包含字段的对象类
     * @param fieldName 字段名称
     * @return  获取不到返回null
     */
    public static Object getValueByFieldName(Object object, String fieldName) throws IllegalAccessException {
        Field field = getFieldByFieldName(object, fieldName);
        Object value = null;
        if (field != null){
            if (field.isAccessible()) {
                value = field.get(object);
            }else {
                field.setAccessible(true);
                value = field.get(object);
                field.setAccessible(false);
            }
        }
        return value;
    }

    /**
     * 设置obejct类中fieldName字段的值value
     * @param object    对象类
     * @param fieldName obejct中的字段
     * @param value 设置fieldName的值
     */
    public static void setValueByFieldName(Object object, String fieldName, Object value) throws IllegalAccessException {
        Field field = getFieldByFieldName(object, fieldName);
        if (field.isAccessible()){
            field.set(object, value);
        }else {
            field.setAccessible(true);
            field.set(object, value);
            field.setAccessible(false);
        }
    }
}
