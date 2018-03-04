package com.evy.jing.daoImpl;

import com.evy.jing.dao.SeRolePermissionDao;
import com.evy.jing.mapper.SeRolePermissionMapper;
import com.evy.jing.model.SeRolePermission;
import com.evy.jing.model.SeRolePermissionExample;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SeRolePermissionDaoImpl implements SeRolePermissionDao {
    @Resource
    SeRolePermissionMapper seRolePermissionMapper;

    @Override
    public int insert(SeRolePermission seRolePermission) {
        return seRolePermissionMapper.insertSelective(seRolePermission);
    }

    @Override
    public int update(SeRolePermission seRolePermission) {
        SeRolePermissionExample seRolePermissionExample = new SeRolePermissionExample();
        SeRolePermissionExample.Criteria criteria = seRolePermissionExample.createCriteria();
        criteria.andIdIsNotNull();

        return seRolePermissionMapper.updateByExampleSelective(seRolePermission, seRolePermissionExample);
    }

    @Override
    public int delete(Integer id) {
        SeRolePermissionExample seRolePermissionExample = new SeRolePermissionExample();
        SeRolePermissionExample.Criteria criteria = seRolePermissionExample.createCriteria();
        criteria.andIdEqualTo(id);
        return seRolePermissionMapper.deleteByExample(seRolePermissionExample);
    }

    /**
     * 根据主键返回唯一的SeRolePermission类
     * @param id
     * @return
     */
    @Override
    public SeRolePermission selectByPrimaryKey(Integer id) {
        SeRolePermissionExample seRolePermissionExample = new SeRolePermissionExample();
        SeRolePermissionExample.Criteria criteria = seRolePermissionExample.createCriteria();
        criteria.andIdEqualTo(id);

        List<SeRolePermission> list = seRolePermissionMapper.selectByExample(seRolePermissionExample);

        return list.size() > 0 ? list.get(0) : null;
    }
}
