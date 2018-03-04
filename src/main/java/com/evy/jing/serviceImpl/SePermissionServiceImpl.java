package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SePermissionDao;
import com.evy.jing.model.SePermission;
import com.evy.jing.service.SePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SePermissionServiceImpl extends BaseServiceImpl<SePermission, Integer>
        implements SePermissionService {
    @Resource
    SePermissionDao sePermissionDao;

    @Override
    BaseDao<SePermission, Integer> getDao() {
        return sePermissionDao;
    }
}
