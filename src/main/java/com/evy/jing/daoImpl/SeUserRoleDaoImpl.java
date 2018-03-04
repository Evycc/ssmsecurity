package com.evy.jing.daoImpl;

import com.evy.jing.dao.SeUserRoleDao;
import com.evy.jing.mapper.SeUserRoleMapper;
import com.evy.jing.model.SeUserRole;
import com.evy.jing.model.SeUserRoleExample;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SeUserRoleDaoImpl implements SeUserRoleDao {
    @Resource
    SeUserRoleMapper seUserRoleMapper;

    @Override
    public int insert(SeUserRole seUserRole) {
        return seUserRoleMapper.insertSelective(seUserRole);
    }

    @Override
    public int update(SeUserRole seUserRole) {
        SeUserRoleExample seUserRoleExample = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = seUserRoleExample.createCriteria();
        criteria.andIdIsNotNull();

        return seUserRoleMapper.updateByExampleSelective(seUserRole, seUserRoleExample);
    }

    @Override
    public int delete(Integer id) {
        SeUserRoleExample seUserRoleExample = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = seUserRoleExample.createCriteria();
        criteria.andIdEqualTo(id);
        return seUserRoleMapper.deleteByExample(seUserRoleExample);
    }

    /**
     * 根据主键返回唯一的SeUserRole类
     * @param id
     * @return  为空则返回null
     */
    @Override
    public SeUserRole selectByPrimaryKey(Integer id) {
        SeUserRoleExample seUserRoleExample = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = seUserRoleExample.createCriteria();
        criteria.andIdEqualTo(id);

        List<SeUserRole> list = seUserRoleMapper.selectByExample(seUserRoleExample);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SeUserRole> findBySeUserId(Integer id) {
        SeUserRoleExample seUserRoleExample = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = seUserRoleExample.createCriteria();
        criteria.andUserIdEqualTo(id);

        List<SeUserRole> list = seUserRoleMapper.selectByExample(seUserRoleExample);
        return list.size() > 0 ? list : null;
    }

    @Override
    public List<SeUserRole> findAllUserRole() {
        SeUserRoleExample example = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();
        return seUserRoleMapper.selectByExample(example);
    }

    @Override
    public int deleteByUserId(Integer userId) {
        SeUserRoleExample example = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        return seUserRoleMapper.deleteByExample(example);
    }

    @Override
    public int insertByUserRoleList(List<SeUserRole> userRoles) {
        return seUserRoleMapper.insertByList(userRoles);
    }

    @Override
    public List<SeUserRole> selectByUserIdAndRoleId(Integer userId, Integer roleId) {
        SeUserRoleExample example = new SeUserRoleExample();
        SeUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        criteria.andRoleIdEqualTo(roleId);
        List<SeUserRole> userRoleList = seUserRoleMapper.selectByExample(example);
        return userRoleList.isEmpty() ? null : userRoleList;
    }

    @Override
    public int delteList(Integer userId, List<SeUserRole> userRoles) {
        return seUserRoleMapper.deleteByList(userId, userRoles);
    }
}
