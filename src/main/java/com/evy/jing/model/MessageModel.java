package com.evy.jing.model;

import org.springframework.web.socket.TextMessage;

import java.io.Serializable;
import java.util.Date;

/**
 * websocket消息体
 * @author : Evy
 * @date : Created 2018/1/28 21:32
 */
public class MessageModel implements Serializable{
    /**
     * 消息体
     */
    private String message;
    /**
     * 消息日期
     */
    private Date date;
    /**
     * 发送人
     */
    private String from;
    /**
     * 接收人
     */
    private String to;

    public MessageModel(){
        this.date = new Date();
    }

    public MessageModel(String message, Date date, String from, String to) {
        this.message = message;
        this.date = date;
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getMessage() {

        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessageModel{" +
                "message=" + message +
                ", date=" + date +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }
}
