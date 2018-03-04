package com.evy.jing.dao;

import com.evy.jing.model.SeUserInfo;

import java.util.List;

public interface SeUserInfoDao extends BaseDao<SeUserInfo, Integer> {
    /**
     * 根据用户主键查找SeUserInfo
     * @param userId
     * @return  查找不到返回null
     */
    List<SeUserInfo> findByUserId(Integer userId);
}
