package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.dao.SeUserDao;
import com.evy.jing.model.SeUser;
import com.evy.jing.pageplugin.PageModel;
import com.evy.jing.security.PasswordEcode;
import com.evy.jing.service.SeUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class SeUserServiceImpl extends BaseServiceImpl<SeUser, Integer> implements SeUserService {
    @Resource
    SeUserDao seUserDao;
    @Resource
    PasswordEcode passwordEncoder;

    @Override
    BaseDao<SeUser, Integer> getDao() {
        return seUserDao;
    }

    @Override
    public List<SeUser> findByUsername(String username) {
        return seUserDao.findByUsername(username);
    }

    @Override
    public List<SeUser> findByEmail(String email) {
        return seUserDao.findByEmail(email);
    }

    @Override
    public boolean isExistUsername(String username) {
        List<SeUser> users = seUserDao.findByUsername(username);
        return users == null ? false : true;
    }

    @Override
    public boolean isExistEmail(String email) {
        List<SeUser> users = seUserDao.findByEmail(email);
        return users == null ? false : true;
    }

    @Override
    public int updateUserPassword(SeUser user) {
        String oldPassword = user.getPassword();
        user.setPassword(passwordEncoder.encodePassword(oldPassword, user.getCredentialsSalt()));
        return super.update(user);
    }

    @Override
    public boolean isEqualsPassword(SeUser user, String newPassword) {
        return passwordEncoder.isPasswordValid(user.getPassword(), newPassword,
                user.getCredentialsSalt());
    }

    @Override
    public List<SeUser> getAllUserByPage(PageModel pageModel) {
        return seUserDao.getAllByPage(pageModel);
    }

    @Override
    public int lockedUser(Integer id) {
        return seUserDao.lockedByUserId(id);
    }

    @Override
    public int unlockUser(Integer id) {
        return seUserDao.unlockUserId(id);
    }

    @Override
    public List<SeUser> findAllUser() {
        return seUserDao.getAllUser();
    }

    @Override
    public int insert(SeUser seUser) {
        String oldPassword = seUser.getPassword();
        seUser.setPassword(passwordEncoder.encodePassword(oldPassword, seUser.getCredentialsSalt()));
        return super.insert(seUser);
    }
}
