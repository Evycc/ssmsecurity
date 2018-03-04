package com.evy.jing.security;

import com.evy.jing.model.SeRole;
import com.evy.jing.model.SeUser;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserService;
import com.evy.jing.util.LoggerUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 验证器
 */
@Service
public class CustomDetailsService implements UserDetailsService {
    @Resource
    SeUserService seUserService;
    @Resource
    SeRoleService seRoleService;

    /**
     * 验证登录账号信息
     *
     * @param s
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        //判断是否用户名登录
        List<SeUser> users = seUserService.findByUsername(s);
        if (users == null) {
            //判断是否邮箱登录
            users = seUserService.findByEmail(s);
        }

        if (users == null) {
            throw new UsernameNotFoundException("User Not Found");
        }

        SeUser seUser = users.get(0);
        String username = seUser.getEmail();

        //为true启用用户
        boolean enable = "true".equalsIgnoreCase(seUser.getEnable());
        //为true帐户尚未过期
        boolean account = "true".equalsIgnoreCase(seUser.getAccount());
        //为true凭证尚未过期
        boolean credentials = "true".equalsIgnoreCase(seUser.getCredentials());
        //为true如果帐户未锁定
        boolean locked = "true".equalsIgnoreCase(seUser.getLocked());

        return new SaltUser(username, seUser.getPassword(), enable, account,
                credentials, locked, getGrantedAuthorities(seUser), seUser.getCredentialsSalt());
    }

    /**
     * 添加角色
     *
     * @param seUser
     * @return
     */
    private Collection<? extends GrantedAuthority> getGrantedAuthorities(SeUser seUser) {
        List<SeRole> roles = seRoleService.findBySeUserId(seUser.getId());
        Assert.notNull(roles, getClass().getSimpleName() + " 用户角色为空!");

        //加上自定义标志
        List<GrantedAuthority> list = new ArrayList<GrantedAuthority>();
        for (SeRole seRole : roles) {
            list.add(new SimpleGrantedAuthority("ROLE_" + seRole.getType()));
        }

        LoggerUtils.debug(getClass(), "roles : %s", list);
        return list;
    }
}
