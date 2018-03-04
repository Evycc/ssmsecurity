package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SeUserRoleDao;
import com.evy.jing.model.SeUserRole;
import com.evy.jing.service.SeUserRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeUserRoleServiceImpl extends BaseServiceImpl<SeUserRole, Integer> implements SeUserRoleService {
    @Resource
    SeUserRoleDao seUserRoleDao;

    @Override
    BaseDao<SeUserRole, Integer> getDao() {
        return seUserRoleDao;
    }

    @Override
    public List<SeUserRole> findByUserId(Integer userId) {
        return seUserRoleDao.findBySeUserId(userId);
    }

    @Override
    public List<SeUserRole> findAll() {
        return seUserRoleDao.findAllUserRole();
    }

    @Override
    public int deleteByUserId(Integer userId) {
        return seUserRoleDao.deleteByUserId(userId);
    }

    @Override
    public int insertList(List<SeUserRole> userRoles) {
        return seUserRoleDao.insertByUserRoleList(userRoles);
    }

    @Override
    public List<SeUserRole> selectByUserIdRoleId(Integer userId, Integer roleId) {
        return seUserRoleDao.selectByUserIdAndRoleId(userId, roleId);
    }

    @Override
    public int deleteList(Integer userId, List<SeUserRole> userRoles) {
        return seUserRoleDao.delteList(userId, userRoles);
    }
}
