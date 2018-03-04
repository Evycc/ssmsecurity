package com.evy.jing.model;

import java.io.Serializable;
import java.util.Date;

public class SeUser implements Serializable{
    private Integer id;

    private String email;

    private String ssoId;

    private String password;

    /**
     * 设置true启用用户
     */
    private String enable;

    /**
     * 设置true用户账户未过期
     */
    private String account;

    /**
     * 设置true凭证尚未过期
     */
    private String credentials;

    /**
     * 设置true账户未锁定
     */
    private String locked;

    private Date createTime;

    private Date lastTime;

    private String salt;

    /**
     * 自定义标识
     */
    private final String mySalt = "evy_salt:";

    public SeUser() {
    }

    /**
     * 注册时可使用该构造函数初始化数值
     * @param email
     * @param password
     */
    public SeUser(String email, String password) {
        this.ssoId = email;
        this.email = email;
        this.password = password;
        this.enable = Boolean.TRUE.toString();
        this.account = Boolean.TRUE.toString();
        this.credentials = Boolean.TRUE.toString();
        this.locked = Boolean.TRUE.toString();
        this.createTime = new Date();
        this.salt = this.ssoId == null ? this.email : ssoId;
    }

    /**
     * 获取标识盐
     * @return
     */
    public String getCredentialsSalt(){
        return mySalt + salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getSsoId() {
        return ssoId;
    }

    public void setSsoId(String ssoId) {
        this.ssoId = ssoId == null ? null : ssoId.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getEnable() {
        return enable;
    }

    public void setEnable(String enable) {
        this.enable = enable == null ? null : enable.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    public String getCredentials() {
        return credentials;
    }

    public void setCredentials(String credentials) {
        this.credentials = credentials == null ? null : credentials.trim();
    }

    public String getLocked() {
        return locked;
    }

    public void setLocked(String locked) {
        this.locked = locked == null ? null : locked.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }

    @Override
    public String toString() {
        return "SeUser{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", ssoId='" + ssoId + '\'' +
                ", password='" + password + '\'' +
                ", enable='" + enable + '\'' +
                ", account='" + account + '\'' +
                ", credentials='" + credentials + '\'' +
                ", locked='" + locked + '\'' +
                ", createTime=" + createTime +
                ", lastTime=" + lastTime +
                ", salt='" + salt + '\'' +
                '}';
    }
}