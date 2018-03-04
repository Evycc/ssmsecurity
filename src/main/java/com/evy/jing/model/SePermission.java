package com.evy.jing.model;

import java.io.Serializable;

public class SePermission implements Serializable {
    private Integer id;

    private String permissionName;

    private String permissionSign;

    private String description;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName == null ? null : permissionName.trim();
    }

    public String getPermissionSign() {
        return permissionSign;
    }

    public void setPermissionSign(String permissionSign) {
        this.permissionSign = permissionSign == null ? null : permissionSign.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    @Override
    public String toString() {
        return "SePermission{" +
                "id=" + id +
                ", permissionName='" + permissionName + '\'' +
                ", permissionSign='" + permissionSign + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}