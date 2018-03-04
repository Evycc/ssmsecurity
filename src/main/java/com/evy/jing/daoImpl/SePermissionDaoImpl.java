package com.evy.jing.daoImpl;

import com.evy.jing.dao.SePermissionDao;
import com.evy.jing.mapper.SePermissionMapper;
import com.evy.jing.model.SePermission;
import com.evy.jing.model.SePermissionExample;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SePermissionDaoImpl implements SePermissionDao {
    @Resource
    SePermissionMapper sePermissionMapper;

    @Override
    public int insert(SePermission sePermission) {
        return sePermissionMapper.insertSelective(sePermission);
    }

    @Override
    public int update(SePermission sePermission) {
        SePermissionExample permissionExample = new SePermissionExample();
        SePermissionExample.Criteria criteria = permissionExample.createCriteria();
        criteria.andIdIsNotNull();
        return sePermissionMapper.updateByExampleSelective(sePermission, permissionExample);
    }

    @Override
    public int delete(Integer id) {
        SePermissionExample permissionExample = new SePermissionExample();
        SePermissionExample.Criteria criteria = permissionExample.createCriteria();
        criteria.andIdEqualTo(id);
        return sePermissionMapper.deleteByExample(permissionExample);
    }

    /**
     * 返回指定主键的唯一SePermission类
     * @param id
     * @return
     */
    @Override
    public SePermission selectByPrimaryKey(Integer id) {
        SePermissionExample permissionExample = new SePermissionExample();
        SePermissionExample.Criteria criteria = permissionExample.createCriteria();
        criteria.andIdEqualTo(id);

        List<SePermission> sePermissions = sePermissionMapper.selectByExample(permissionExample);
        return sePermissions.size() > 0 ? sePermissions.get(0) : null;
    }
}
