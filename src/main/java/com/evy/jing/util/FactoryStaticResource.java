package com.evy.jing.util;

import com.evy.jing.model.ExcludeCacheModel;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.redis.JedisManager;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserRoleService;
import com.evy.jing.service.SeUserService;
import com.evy.jing.websocket.MessageHandler;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 工厂类，返回静态变量实例
 * @author : Evy
 * @date : Created 2018/1/23 14:57
 */
@Component
public class FactoryStaticResource {
    private static JedisManager jedisManager;
    private static ExcludeCacheModel excludeCacheModel;
    private static SessionRegistry sessionRegistry;
    private static SeUserService seUserService;
    private static SeRoleService seRoleService;
    private static SeUserRoleService seUserRoleService;
    private static JedisCache cache;
    private static MessageHandler messageHandler;
    private static MimeMessageHelper mimeMessageHelper;
    private static JavaMailSender mailSender;

    public static MimeMessageHelper getMimeMessageHelper() {
        return mimeMessageHelper;
    }

    @Resource
    public void setMimeMessageHelper(MimeMessageHelper mimeMessageHelper) {
        FactoryStaticResource.mimeMessageHelper = mimeMessageHelper;
    }

    public static JavaMailSender getMailSender() {
        return mailSender;
    }

    @Resource
    public void setMailSender(JavaMailSender mailSender) {
        FactoryStaticResource.mailSender = mailSender;
    }

    public static MessageHandler getMessageHandler() {
        return messageHandler;
    }

    @Resource
    public void setMessageHandler(MessageHandler messageHandler) {
        FactoryStaticResource.messageHandler = messageHandler;
    }

    public static SessionRegistry getSessionRegistry() {
        return sessionRegistry;
    }

    @Resource
    public void setSessionRegistry(SessionRegistry sessionRegistry) {
        FactoryStaticResource.sessionRegistry = sessionRegistry;
    }

    public static SeUserService getSeUserService() {
        return seUserService;
    }

    @Resource
    public void setSeUserService(SeUserService seUserService) {
        FactoryStaticResource.seUserService = seUserService;
    }

    public static SeRoleService getSeRoleService() {
        return seRoleService;
    }

    @Resource
    public void setSeRoleService(SeRoleService seRoleService) {
        FactoryStaticResource.seRoleService = seRoleService;
    }

    public static SeUserRoleService getSeUserRoleService() {
        return seUserRoleService;
    }

    @Resource
    public void setSeUserRoleService(SeUserRoleService seUserRoleService) {
        FactoryStaticResource.seUserRoleService = seUserRoleService;
    }

    public static JedisCache getCache() {
        return cache;
    }

    @Resource
    public void setCache(JedisCache cache) {
        FactoryStaticResource.cache = cache;
    }

    public static ExcludeCacheModel getExcludeCacheModel() {
        return excludeCacheModel;
    }

    @Resource
    public void setExcludeCacheModel(ExcludeCacheModel excludeCacheModel) {
        FactoryStaticResource.excludeCacheModel = excludeCacheModel;
    }

    public static JedisManager getJedisManager() {
        return FactoryStaticResource.jedisManager;
    }

    @Resource
    public void setJedisManager(JedisManager jedisManager) {
        FactoryStaticResource.jedisManager = jedisManager;
    }
}
