package com.evy.jing.daoImpl;

import com.evy.jing.dao.SeRoleDao;
import com.evy.jing.dao.SeUserRoleDao;
import com.evy.jing.mapper.SeRoleMapper;
import com.evy.jing.model.SeRole;
import com.evy.jing.model.SeRoleExample;
import com.evy.jing.model.SeUserRole;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class SeRoleDaoImpl implements SeRoleDao {
    @Resource
    SeRoleMapper seRoleMapper;
    @Resource
    SeUserRoleDao seUserRoleDao;

    @Override
    public int insert(SeRole seRole) {
        return seRoleMapper.insertSelective(seRole);
    }

    @Override
    public int update(SeRole seRole) {
        SeRoleExample seRoleExample = new SeRoleExample();
        SeRoleExample.Criteria criteria = seRoleExample.createCriteria();
        criteria.andIdIsNotNull();
        return seRoleMapper.updateByExampleSelective(seRole, seRoleExample);
    }

    @Override
    public int delete(Integer id) {
        SeRoleExample seRoleExample = new SeRoleExample();
        SeRoleExample.Criteria criteria = seRoleExample.createCriteria();
        criteria.andIdEqualTo(id);
        return seRoleMapper.deleteByExample(seRoleExample);
    }

    /**
     * 根据主键返回唯一的SeRole类
     * @param id
     * @return  查找不到返回null
     */
    @Override
    public SeRole selectByPrimaryKey(Integer id) {
        SeRoleExample seRoleExample = new SeRoleExample();
        SeRoleExample.Criteria criteria = seRoleExample.createCriteria();
        criteria.andIdEqualTo(id);

        List<SeRole> seRoleList = seRoleMapper.selectByExample(seRoleExample);
        return seRoleList.size() > 0 ? seRoleList.get(0) : null;
    }

    /**
     * 根据SeUser主键查找SeRole类
     * @param id
     * @return  查找不到返回null
     */
    @Override
    public List<SeRole> findBySeUserId(Integer id) {
        List<SeUserRole> userRoles = seUserRoleDao.findBySeUserId(id);
        if (userRoles == null){
            return null;
        }

        List<SeRole> roles = new ArrayList<SeRole>();
        for (SeUserRole userRole : userRoles){
            SeRole seRole = this.selectByPrimaryKey(userRole.getRoleId());
            if (seRole != null){
                roles.add(seRole);
            }
        }
        return roles.isEmpty() ? null : roles;
    }

    @Override
    public SeRole findByType(String type) {
        SeRoleExample seRoleExample = new SeRoleExample();
        SeRoleExample.Criteria criteria = seRoleExample.createCriteria();
        criteria.andTypeEqualTo(type);

        List<SeRole> roles = seRoleMapper.selectByExample(seRoleExample);

        return roles.size() > 0 ? roles.get(0) : null;
    }

    @Override
    public List<SeRole> getAllRole() {
        SeRoleExample example = new SeRoleExample();
        SeRoleExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();
        return seRoleMapper.selectByExample(example);
    }
}
