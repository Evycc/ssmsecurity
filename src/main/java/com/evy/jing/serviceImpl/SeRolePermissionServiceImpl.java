package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SeRolePermissionDao;
import com.evy.jing.model.SeRolePermission;
import com.evy.jing.service.SeRolePermissionService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeRolePermissionServiceImpl extends BaseServiceImpl<SeRolePermission, Integer>
        implements SeRolePermissionService {
    @Resource
    SeRolePermissionDao seRolePermissionDao;

    @Override
    BaseDao<SeRolePermission, Integer> getDao() {
        return seRolePermissionDao;
    }
}
