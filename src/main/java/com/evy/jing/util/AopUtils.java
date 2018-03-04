package com.evy.jing.util;

import com.evy.jing.model.ExcludeCacheModel;
import org.aspectj.lang.JoinPoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * 用于定义切入方法类
 */
public class AopUtils {
    private static Logger logger;

    /**
     * 记录当前即将执行类方法
     * @param jp
     */
    public void beforeLog(JoinPoint jp){
        logger = LoggerFactory.getLogger("myLog");
        logger.info("{} 执行:{}" , jp.getTarget().getClass(), jp.getSignature().getName());
    }
}
