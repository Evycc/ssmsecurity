package com.evy.jing.model;

import java.io.Serializable;

public class SeUserInfo implements Serializable {
    private Integer id;

    private Integer userId;

    private String userHeadId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserHeadId() {
        return userHeadId;
    }

    public void setUserHeadId(String userHeadId) {
        this.userHeadId = userHeadId == null ? null : userHeadId.trim();
    }
}