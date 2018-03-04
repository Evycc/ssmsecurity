package com.evy.jing.daoImpl;

import com.evy.jing.dao.SeUserDao;
import com.evy.jing.mapper.SeUserMapper;
import com.evy.jing.model.SeUser;
import com.evy.jing.model.SeUserExample;
import com.evy.jing.model.SeUserRole;
import com.evy.jing.pageplugin.PageModel;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserRoleService;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

@Repository
public class SeUserDaoImpl implements SeUserDao {
    @Resource
    SeUserMapper seUserMapper;
    @Resource
    SeUserRoleService seUserRoleService;

    @Override
    public int insert(SeUser seUser) {
        return seUserMapper.insertSelective(seUser);
    }

    @Override
    public int update(SeUser seUser) {
        SeUserExample seUserExample = new SeUserExample();
        SeUserExample.Criteria criteria = seUserExample.createCriteria();
        criteria.andIdEqualTo(seUser.getId());
        return seUserMapper.updateByExampleSelective(seUser, seUserExample);
    }

    @Override
    public int delete(Integer id) {
        //删除用户-角色关联的记录
        seUserRoleService.deleteByUserId(id);

        SeUserExample seUserExample = new SeUserExample();
        SeUserExample.Criteria criteria = seUserExample.createCriteria();
        criteria.andIdEqualTo(id);
        return seUserMapper.deleteByExample(seUserExample);
    }

    @Override
    public SeUser selectByPrimaryKey(Integer id) {
        SeUserExample seUserExample = new SeUserExample();
        SeUserExample.Criteria criteria = seUserExample.createCriteria();
        criteria.andIdEqualTo(id);

        List<SeUser> list = seUserMapper.selectByExample(seUserExample);
        return list.size() > 0 ? list.get(0) : null;
    }

    @Override
    public List<SeUser> findByUsername(String username) {
        SeUserExample seUserExample = new SeUserExample();
        SeUserExample.Criteria criteria = seUserExample.createCriteria();
        criteria.andSsoIdEqualTo(username);

        List<SeUser> seUsers = seUserMapper.selectByExample(seUserExample);
        return seUsers.isEmpty() ? null : seUsers;
    }

    @Override
    public List<SeUser> findByEmail(String email) {
        SeUserExample seUserExample = new SeUserExample();
        SeUserExample.Criteria criteria = seUserExample.createCriteria();
        criteria.andEmailEqualTo(email);

        List<SeUser> seUsers = seUserMapper.selectByExample(seUserExample);
        return seUsers.isEmpty() ? null : seUsers;
    }

    @Override
    public List<SeUser> getAllByPage(PageModel pageModel) {
        List<SeUser> userList = seUserMapper.selectListPage(pageModel);
        return userList.isEmpty() ? null : userList;
    }

    @Override
    public int lockedByUserId(Integer userId) {
        SeUser user = selectByPrimaryKey(userId);
        if (user != null){
            user.setLocked(Boolean.FALSE.toString());
            return update(user);
        }
        return 0;
    }

    @Override
    public int unlockUserId(Integer userId) {
        SeUser user = selectByPrimaryKey(userId);
        if (user != null){
            user.setLocked(Boolean.TRUE.toString());
            return update(user);
        }
        return 0;
    }

    @Override
    public List<SeUser> getAllUser() {
        SeUserExample example = new SeUserExample();
        SeUserExample.Criteria criteria = example.createCriteria();
        criteria.andIdIsNotNull();
        return seUserMapper.selectByExample(example);
    }
}
