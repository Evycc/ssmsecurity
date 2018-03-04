package com.evy.jing.service;

import com.evy.jing.model.SeUserInfo;

public interface SeUserInfoService extends BaseService<SeUserInfo, Integer> {
    /**
     * 根据用户主键查找SeUserInfo
     * @param userId
     * @return  查找不到返回null
     */
    SeUserInfo findByUserId(Integer userId);
}
