package com.evy.jing.websocket;

import com.evy.jing.model.MessageModel;

/**
 * 消息推送器接口
 *
 * @author : Evy
 * @date : Created 2018/1/28 20:45
 */
public interface MessagePushInterface {
    /**
     * 单点发送消息(默认发送普通消息)
     *
     * @param messageModel 消息体
     * @return 发送成功返回true
     */
    boolean sendToUser(MessageModel messageModel);

    /**
     * 群发消息(默认发送普通消息)
     *
     * @param messageModel
     */
    void sendToUsers(MessageModel messageModel);

    /**
     * 接收并发送离线信息
     *
     * @param from 接收离线信息的用户
     * @param to   发送离线信息的用户
     * @return 发送离线信息成功返回true
     */
    boolean sendOfflineMessage(String from, String to);

    /**
     * 通知用户离线
     *
     * @param name     用户名
     * @param isOnline 在线为true，反之为false
     */
    void noticeIsOnlinebyUsername(String name, Boolean isOnline);

    /**
     * 获取from用户与to用户之间的消息记录
     *
     * @param from 用户
     * @param to   用户
     * @return
     */
    Boolean getMessageRecord(String from, String to);
}
