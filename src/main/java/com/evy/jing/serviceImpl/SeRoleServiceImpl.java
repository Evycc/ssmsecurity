package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SeRoleDao;
import com.evy.jing.model.SeRole;
import com.evy.jing.service.SeRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeRoleServiceImpl extends BaseServiceImpl<SeRole, Integer> implements SeRoleService {
    @Resource
    SeRoleDao seRoleDao;

    @Override
    BaseDao<SeRole, Integer> getDao() {
        return seRoleDao;
    }

    @Override
    public List<SeRole> findBySeUserId(Integer id) {
        return seRoleDao.findBySeUserId(id);
    }

    @Override
    public SeRole findByType(String role) {
        return seRoleDao.findByType(role);
    }

    @Override
    public List<SeRole> findAllRole() {
        return seRoleDao.getAllRole();
    }
}
