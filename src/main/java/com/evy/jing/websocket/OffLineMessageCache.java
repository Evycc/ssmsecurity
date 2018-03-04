package com.evy.jing.websocket;

import com.evy.jing.EnumMsg.EnumSendMessage;
import com.evy.jing.model.MessageModel;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.util.FactoryStaticResource;
import com.evy.jing.util.LoggerUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * websocket离线消息缓存
 *
 * @author : Evy
 * @date : Created 2018/1/28 20:58
 */
class OffLineMessageCache {
    @Resource
    private static JedisCache cache;

    static {
        cache = FactoryStaticResource.getCache();
        cache.setTimeOut(0);
    }

    /**
     * 缓存离线信息
     *
     * @param messageModel 消息体
     */
    static void putFromToMessage(MessageModel messageModel) {
        putMessage(EnumSendMessage.CACHE_OFFLINE_MESSAGE, messageModel);
    }

    /**
     * 获取from用户与to用户的离线信息
     *
     * @param from 接受离线信息的用户
     * @param to   发送离线信息的用户
     * @return 返回离线信息集合，不存在则返回null
     */
    static List<MessageModel> getOfflineMessage(String from, String to) {
        return getMessage(EnumSendMessage.CACHE_OFFLINE_MESSAGE, from, to);
    }

    /**
     * 获取from用户与to用户之间的消息
     * @param from  用户
     * @param to    用户
     * @return  返回离线信息体集合，不存在则返回null
     */
    static List<MessageModel> getMessageRecord(String from, String to) {
        return getMessage(EnumSendMessage.CACHE_MESSAGE_RECORD, from, to);
    }

    /**
     * 删除缓存的离线信息
     *
     * @param from 接受离线信息的用户
     * @param to   发送离线信息的用户
     */
    static void deleteOfflineMessage(String from, String to) {
        deleteMessage(EnumSendMessage.CACHE_OFFLINE_MESSAGE, from, to);
    }

    /**
     * 记录用户消息记录
     *
     * @param model 消息体
     */
    static void putMessageRecord(MessageModel model) {
        putMessage(EnumSendMessage.CACHE_MESSAGE_RECORD, model);
    }

    private static void deleteMessage(EnumSendMessage enumSendMessage, String from, String to){
        String cacheKey = enumSendMessage.getReasonPhrase() + from;
        Map<String, List<MessageModel>> offLineMap = (Map<String, List<MessageModel>>) cache.get(cacheKey);

        if ((offLineMap != null) && (offLineMap.containsKey(to))) {
            offLineMap.remove(to);
            if (offLineMap.size() == 0) {
                cache.remove(cacheKey);
            } else {
                cache.update(cacheKey, offLineMap);
            }
        }
    }

    /**
     * 获取from用户与to用户之间的消息
     *
     * @param enumSendMessage 消息标识
     * @param from            用户
     * @param to              用户
     * @return 返回离线信息体集合，不存在则返回null
     */
    private static List<MessageModel> getMessage(EnumSendMessage enumSendMessage, String from, String to) {
        LoggerUtils.debug(OffLineMessageCache.class, "查找离线信息(from:%s,to:%s)\n" +
                "离线数量:%s", from, to, cache.size());
        List<MessageModel> messageModels = null;
        String cacheKey = enumSendMessage.getReasonPhrase() + from;

        Map<String, List<MessageModel>> offLineMap = (Map<String, List<MessageModel>>) cache.get(cacheKey);
        if ((offLineMap != null) && (offLineMap.containsKey(to))) {
            messageModels = offLineMap.get(to);
        }
        return messageModels;
    }

    /**
     * 缓存消息
     *
     * @param enumSendMessage 缓存标识
     * @param messageModel    消息体<br>
     *                        (k,v)    k:发送人 v:缓存的消息体集合
     */
    private static void putMessage(EnumSendMessage enumSendMessage, MessageModel messageModel) {
        //缓存键
        String cacheKey = enumSendMessage.getReasonPhrase() + messageModel.getTo();
        Map<String, List<MessageModel>> offLineMap = (Map<String, List<MessageModel>>) cache.get(cacheKey);

        if (offLineMap != null) {
            if (offLineMap.containsKey(messageModel.getFrom())) {
                //已存在的发信人，直接添加
                offLineMap.get(messageModel.getFrom()).add(messageModel);
                cache.put(cacheKey, offLineMap);
                LoggerUtils.debug(OffLineMessageCache.class, "缓存%s信息(keys:%s," +
                        "value:%s)", enumSendMessage, cacheKey, offLineMap);
                return;
            }
        } else {
            offLineMap = new HashMap<String, List<MessageModel>>(4);
        }

        //否则，新建离线信息数组
        List<MessageModel> models = new ArrayList<MessageModel>(4);
        models.add(messageModel);
        offLineMap.put(messageModel.getFrom(), models);

        cache.put(cacheKey, offLineMap);
        LoggerUtils.debug(OffLineMessageCache.class, "缓存%s信息(keys:%s," +
                "value:%s)", enumSendMessage, cacheKey, offLineMap);
    }
}
