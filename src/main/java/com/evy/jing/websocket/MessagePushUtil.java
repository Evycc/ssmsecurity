package com.evy.jing.websocket;

import com.evy.jing.EnumMsg.EnumSendMessage;
import com.evy.jing.model.MessageModel;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * websocket消息推送器
 *
 * @author : Evy
 * @date : Created 2018/1/28 20:45
 */
@Component
public class MessagePushUtil implements MessagePushInterface {
    @Resource
    private MessageHandler handler;

    @Override
    public boolean sendToUser(MessageModel messageModel) {
        return handler.sendToUser(messageModel, EnumSendMessage.WS_ORDINRY);
    }

    @Override
    public void sendToUsers(MessageModel messageModel) {
        handler.sendToUsers(messageModel, EnumSendMessage.WS_ORDINRY);
    }

    @Override
    public boolean sendOfflineMessage(String from, String to) {
        return handler.sendOfflineMessage(from, to);
    }

    @Override
    public void noticeIsOnlinebyUsername(String name, Boolean isOnline) {
        handler.noticeAllIsOnline(name, isOnline);
    }

    @Override
    public Boolean getMessageRecord(String from, String to) {
        return handler.getMessageRecord(from, to);
    }
}
