package com.evy.jing.websocket;

import com.alibaba.druid.support.json.JSONUtils;
import com.evy.jing.EnumMsg.EnumSendMessage;
import com.evy.jing.model.MessageModel;
import com.evy.jing.security.SaltUser;
import com.evy.jing.util.LoggerUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.server.WebSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.*;

/**
 * websocket消息处理器
 *
 * @author : Evy
 * @date : Created 2018/1/28 0:00
 */
@Component
public class MessageHandler extends TextWebSocketHandler {
    /**
     * 当前连接用户
     */
    private static final Map<SaltUser, WebSocketSession> users;

    static {
        users = new HashMap<SaltUser, WebSocketSession>(10);
    }

    /**
     * 客户端调用onopen()方法时触发
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Object o = session.getAttributes().get("username");
        if ((o != null) && (o instanceof SaltUser)){
            users.put((SaltUser) o, session);
        }
        LoggerUtils.debug(MessageHandler.class, "所有登录用户:\n%s", users);
        super.afterConnectionEstablished(session);
    }

    /**
     * 客户端调用send()方法时触发
     *
     * @param session
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        super.handleTextMessage(session, message);
    }

    /**
     * websocket消息传输错误时触发
     *
     * @param session
     * @param exception
     * @throws Exception
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        LoggerUtils.debug(getClass(), "用户异常退出。");
        if (session.isOpen()) {
            session.close();
        }
        super.handleTransportError(session, exception);
    }

    /**
     * 客户端调用close()方法时触发
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        LoggerUtils.debug(getClass(), "用户退出。");
        super.afterConnectionClosed(session, status);
    }

    /**
     * 单发消息
     *
     * @param messageModel 存储着消息的模型
     * @return 发送完成或存储离线信息，返回true
     */
    public boolean sendToUser(MessageModel messageModel, EnumSendMessage enumSend) {
        final boolean[] isSend = {false};

        users.forEach((k, v) -> {
            try {
                if (k.getUsername().equalsIgnoreCase(messageModel.getTo())) {
                    //用户在线，则进行推送
                    if (v.isOpen()) {
                        v.sendMessage(new TextMessage(buildSendMessage(messageModel, enumSend)));
                    } else {
                        //否则，存储到用户离线消息
                        OffLineMessageCache.putFromToMessage(messageModel);
                    }
                    isSend[0] = true;
                    return;
                }
            } catch (IOException e) {
                LoggerUtils.error(getClass(), e, "单发信息出现错误");
            }
        });
        if (!isSend[0]) {
            //收信人未连接，存储到收信人的离线信息
            OffLineMessageCache.putFromToMessage(messageModel);
            isSend[0] = true;
        }
        if (enumSend != EnumSendMessage.WS_OFFLINE){
            //非离线消息，存储用户间对话记录
            OffLineMessageCache.putMessageRecord(messageModel);
        }

        return isSend[0];
    }

    /**
     * 发送指定用户
     * @param session   用户session
     * @param message   发送消息字符串
     * @param enumSendMessage   发送标识
     * @return  发送成功返回true
     */
    public Boolean sendToUser(WebSocketSession session, String message, EnumSendMessage enumSendMessage){
        final boolean[] isSend = {false};
        users.forEach((k, v) -> {
            if ((v == session) && (v.isOpen())){
                try {
                    v.sendMessage(new TextMessage(buildSendMessage(message, enumSendMessage)));
                } catch (IOException e) {
                    LoggerUtils.error(getClass(), e, "单发信息出现错误");
                }
            }
        });
        return isSend[0];
    }

    /**
     * 发送指定用户
     *
     * @param from         指定用户
     * @param messageModel 消息体
     * @param enumSend     发送标识
     * @return 发送成功返回true
     */
    public boolean sendToUser(String from, MessageModel messageModel, EnumSendMessage enumSend) {
        final boolean[] isSend = {false};

        users.forEach((k, v) -> {
            try {
                if (k.getUsername().equalsIgnoreCase(from)) {
                    //用户在线，则进行推送
                    if (v.isOpen()) {
                        v.sendMessage(new TextMessage(buildSendMessage(messageModel, enumSend)));
                    }
                    isSend[0] = true;
                    return;
                }
            } catch (IOException e) {
                LoggerUtils.error(getClass(), e, "单发信息出现错误");
            }
        });
        return isSend[0];
    }

    /**
     * 群发
     *
     * @param messageModel 消息体
     * @param enumSend     消息标志
     */
    public void sendToUsers(MessageModel messageModel, EnumSendMessage enumSend) {
        String json = buildSendMessage(messageModel, enumSend);
        TextMessage textMessage = new TextMessage(json);
        sendToUsers(textMessage);
    }

    /**
     * 群发
     *
     * @param message  消息字符串
     * @param enumSend 发送消息标识
     */
    public void sendToUsers(String message, EnumSendMessage enumSend) {
        String json = buildSendMessage(message, enumSend);
        TextMessage textMessage = new TextMessage(json);
        sendToUsers(textMessage);
    }

    /**
     * 群发
     *
     * @param textMessage 消息体
     */
    private void sendToUsers(TextMessage textMessage) {
        users.forEach((k, v) -> {
            try {
                //用户在线，则进行推送
                if (v.isOpen()) {
                    v.sendMessage(textMessage);
                }
            } catch (IOException e) {
                LoggerUtils.error(getClass(), e, "群发信息出现错误");
            }
        });
    }

    /**
     * 发送离线信息
     *
     * @param from 接收离线信息的用户
     * @param to   发送离线信息的用户
     * @return 发送成功返回true
     */
    public boolean sendOfflineMessage(String from, String to) {
        boolean isSend = false;
        List<MessageModel> messageModels = OffLineMessageCache.getOfflineMessage(from, to);
        if (messageModels != null) {
            //按时间排序离线信息
            sortMessageModelList(messageModels);
            //发送离线信息
            try {
                messageModels.forEach(m -> sendToUser(m, EnumSendMessage.WS_OFFLINE));
                isSend = true;
            } catch (Exception e) {
                isSend = false;
                LoggerUtils.error(getClass(), e, "发送离线信息出现错误");
            }
        }
        if (isSend) {
            //发送离线信息成功，删除缓存的离线信息
            OffLineMessageCache.deleteOfflineMessage(from, to);
        }
        return isSend;
    }

    /**
     * 通知所有用户在线状况
     *
     * @param name     用户名
     * @param isOnline 在线为true，反之为false
     */
    public void noticeAllIsOnline(String name, Boolean isOnline) {
        if (isOnline) {
            //在线
            sendToUsers(name, EnumSendMessage.WS_ISONLINE);
        } else {
            //离线
            sendToUsers(name, EnumSendMessage.WS_ISOFFLINE);
        }
    }

    /**
     * 获取from用户与to用户之间的消息记录
     *
     * @param from 用户
     * @param to   用户
     * @return 获取成功返回true，反之为false
     */
    public Boolean getMessageRecord(String from, String to) {
        Boolean isSuccess = false;
        List<MessageModel> models1 = OffLineMessageCache.getMessageRecord(from, to);
        List<MessageModel> models2 = OffLineMessageCache.getMessageRecord(to, from);
        List<MessageModel> modelsAll;

        if ((models1 != null) && (models2 != null)) {
            models1.addAll(models2);
            modelsAll = models1;
        } else if (models1 != null) {
            modelsAll = models1;
        } else {
            modelsAll = models2;
        }

        if (modelsAll != null) {
            sortMessageModelList(modelsAll);

            try {
                modelsAll.forEach(m -> sendToUser(from, m, EnumSendMessage.WS_ORDINRY));
                isSuccess = true;
            } catch (Exception e) {
                isSuccess = false;
                LoggerUtils.error(getClass(), e, "获取聊天信息出现错误");
            }
        }

        return isSuccess;
    }

    /**
     * 按时间升序排列
     *
     * @param list 排序的集合
     */
    private void sortMessageModelList(List<MessageModel> list) {
        list.sort((m1, m2) -> {
            Date d1 = m1.getDate();
            Date d2 = m2.getDate();
            if (d1.before(d2)) {
                return -1;
            } else if (d1.after(d2)) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    /**
     * 构造键值对形成的json字符串
     *
     * @param message  消息字符串
     * @param enumSend 消息标识
     * @return 返回json字符串
     */
    private String buildSendMessage(String message, EnumSendMessage enumSend) {
        Map<String, String> map = new HashMap<String, String>(1);
        ObjectMapper mapper = new ObjectMapper();

        map.put(enumSend.getReasonPhrase(), message);
        String json = null;
        try {
            json = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * 构造键值对形成的json字符串
     *
     * @param messageModel 消息体
     * @param enumSend     发送标识
     * @return 返回json字符串
     */
    private String buildSendMessage(MessageModel messageModel, EnumSendMessage enumSend) {
        Map<String, MessageModel> map = new HashMap<String, MessageModel>(1);
        ObjectMapper mapper = new ObjectMapper();

        map.put(enumSend.getReasonPhrase(), messageModel);
        String json = null;
        try {
            json = mapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return json;
    }
}