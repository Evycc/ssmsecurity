package com.evy.jing.EnumMsg;

/**
 * websocket发送信息短语
 * @author : Evy
 * @date : Created 2018/1/28 22:04
 */
public enum EnumSendMessage {
    /**
     * 发送人
     */
    FROM("from"),
    /**
     * 接收人
     */
    TO("to"),
    /**
     * 消息正文
     */
    MESSAGE("message"),
    /**
     * 时间
     */
    DATE("date"),
    /**
     * 表示离线信息缓存
     */
    CACHE_OFFLINE_MESSAGE("cache-offline-message"),
    /**
     * 表示聊天记录缓存
     */
    CACHE_MESSAGE_RECORD("cache-message-record"),
    /**
     * 用户断开连接或超时
     */
    WS_OVERTIME("ws-overtime"),
    /**
     * 在线标识
     */
    WS_ISONLINE("ws-isOnline"),
    /**
     * 离线标识
     */
    WS_ISOFFLINE("ws-isOffline"),
    /**
     * 普通消息标识
     */
    WS_ORDINRY("ws-ordinary"),
    /**
     * 离线消息标识
     */
    WS_OFFLINE("ws-offline");

    private final String reasonPhrase;
    EnumSendMessage(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    /**
     * 获取字符串形式
     * @return  返回对应字符串
     */
    public String getReasonPhrase() {
        return reasonPhrase;
    }
}
