package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SeUserInfoDao;
import com.evy.jing.model.SeUserInfo;
import com.evy.jing.service.SeUserInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeUserInfoServiceImpl extends BaseServiceImpl<SeUserInfo, Integer>
        implements SeUserInfoService {
    @Resource
    SeUserInfoDao seUserInfoDao;

    @Override
    BaseDao<SeUserInfo, Integer> getDao() {
        return seUserInfoDao;
    }

    @Override
    public SeUserInfo findByUserId(Integer userId) {
        List<SeUserInfo> list = seUserInfoDao.findByUserId(userId);
        return list == null ? null : list.get(0);
    }
}
