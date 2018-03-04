package com.evy.jing.util;

import com.sun.istack.internal.logging.Logger;
import org.aspectj.lang.JoinPoint;
/**
 * 记录程序执行步骤
 */
public class MyLog {
    static Logger logger;

    public void beforeLog(JoinPoint jp){
        logger = Logger.getLogger(jp.getTarget().getClass());
        logger.info("执行: " + jp.getTarget() + " " + jp.getSignature().getName());
        System.out.println("执行: " + jp.getTarget() + " " + jp.getSignature().getName());
    }
}
