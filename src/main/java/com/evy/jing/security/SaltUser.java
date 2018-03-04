package com.evy.jing.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * 添加salt字段的User
 */
public class SaltUser extends User {
    private String salt;

    public SaltUser(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, String salt) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.salt = salt;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    @Override
    public boolean equals(Object rhs) {
        if (this == rhs){
            return true;
        }
        if (rhs == null || (rhs.getClass() != getClass())){
            return false;
        }
        return super.equals(rhs);
    }
}
