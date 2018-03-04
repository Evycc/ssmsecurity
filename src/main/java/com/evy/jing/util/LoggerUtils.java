package com.evy.jing.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class LoggerUtils {
    private static Map<Class, Logger> loggerMap;

    static {
        loggerMap = new HashMap<Class, Logger>(10);
    }

    /**
     * 输出DEBUG级别日志
     *
     * @param clazz
     * @param message
     */
    public static void debug(Class clazz, String message) {
        Logger logger;
        if (loggerMap.containsKey(clazz)) {
            logger = loggerMap.get(clazz);
        } else {
            logger = LoggerFactory.getLogger(clazz);
            loggerMap.put(clazz, logger);
        }
        logger.debug(message);
    }

    /**
     * 按格式化字符串输出DEBUG级别日志信息
     *
     * @param clazz
     * @param formatStr
     * @param value
     */
    public static void debug(Class<?> clazz, String formatStr, Object... value) {
        if (isNullStr(formatStr)) {
            return;
        }
        if (value != null && value.length != 0) {
            formatStr = String.format(formatStr, value);
        }

        debug(clazz, formatStr);
    }

    /**
     * 输出带异常信息的ERROR级别日志信息
     *
     * @param clazz
     * @param e
     * @param message
     */
    public static void error(Class<?> clazz, Exception e, String message) {
        Logger logger;
        if (loggerMap.containsKey(clazz)) {
            logger = loggerMap.get(clazz);
        } else {
            logger = LoggerFactory.getLogger(clazz);
            loggerMap.put(clazz, logger);
        }
        if (e == null) {
            logger.error(message);
            return;
        }
        logger.error(message, e);
    }

    /**
     * 输出ERROR级别日志信息
     *
     * @param clazz
     * @param message
     */
    public static void error(Class<?> clazz, String message) {
        error(clazz, null, message);
    }

    /**
     * 按格式化字符串输出带异常信息的ERROR级别日志信息
     *
     * @param clazz
     * @param e         异常类
     * @param formatStr
     * @param value
     */
    public static void errorStr(Class<?> clazz, Exception e, String formatStr, Object... value) {
        if (isNullStr(formatStr)) {
            return;
        }
        if (value != null && value.length != 0) {
            formatStr = String.format(formatStr, value);
        }

        error(clazz, e, formatStr);
    }

    /**
     * 按格式化字符串输出ERROR级别日志信息
     *
     * @param clazz
     * @param formatStr
     * @param value
     */
    public static void errorStr(Class<?> clazz, String formatStr, Object... value) {
        if (isNullStr(formatStr)) {
            return;
        }
        if (value != null && value.length != 0) {
            formatStr = String.format(formatStr, value);
        }

        error(clazz, null, formatStr);
    }

    /**
     * 输出INFO级别日志信息
     *
     * @param clazz
     * @param message
     */
    public static void info(Class<?> clazz, String message) {
        Logger logger;
        if (loggerMap.containsKey(clazz)) {
            logger = loggerMap.get(clazz);
        } else {
            logger = LoggerFactory.getLogger(clazz);
            loggerMap.put(clazz, logger);
        }
        logger.info(message);
    }

    /**
     * 按格式化字符串输出INFO级别日志信息
     *
     * @param clazz
     * @param formatStr
     * @param value
     */
    public static void info(Class<?> clazz, String formatStr, Object... value) {
        if (isNullStr(formatStr)) {
            return;
        }
        if (value != null && value.length != 0) {
            formatStr = String.format(formatStr, value);
        }

        info(clazz, formatStr);
    }

    /**
     * 判断字符串是否为空，为空则记录异常
     *
     * @param formatStr
     * @return
     */
    private static boolean isNullStr(String formatStr) {
        if (formatStr == null || "".equals(formatStr)) {
            error(LoggerUtils.class, new NullPointerException(), "formatStr");
            return true;
        }
        return false;
    }
}
