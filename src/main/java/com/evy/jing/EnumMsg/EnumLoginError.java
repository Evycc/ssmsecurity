package com.evy.jing.EnumMsg;

/**
 * 自定义常见登陆错误枚举
 */
public enum EnumLoginError {
    /**
     * Security默认登录错误属性
     */
    SPRING_SECURITY_LAST_EXCEPTION,
    /**
     * 密码更改，要求重新登录
     */
    CHANGE_PASSWORD,
    /**
     * 没有权限
     */
    ERROR_ACCESS,
    /**
     * 账号或密码错误
     */
    ERROR_LOGIN_PASSWORD,
    /**
     * 账号为锁定状态
     */
    ERROR_LOGIN_LOCKED,
    /**
     * 账号未启动
     */
    ERROR_LOGIN_DISABLE,
    /**
     * 密码凭证已过期
     */
    ERROR_LOGIN_CREDENTIAL,
    /**
     * 未知登陆错误
     */
    ERROR_OTHER,
    /**
     * 用户踢出
     */
    ERROR_KICKOUT,
}
