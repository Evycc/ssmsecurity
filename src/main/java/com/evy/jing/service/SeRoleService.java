package com.evy.jing.service;

import com.evy.jing.model.SeRole;

import java.util.List;

public interface SeRoleService extends BaseService<SeRole, Integer> {
    /**
     * 根据用户ID查找角色
     * @param id
     * @return  查找不到返回null
     */
    List<SeRole> findBySeUserId(Integer id);

    /**
     * 根据角色名查找SeRole
     * @param role
     * @return  查找不到返回null
     */
    SeRole findByType(String role);

    /**
     * 查找全部角色
     * @return  全部角色数组
     */
    List<SeRole> findAllRole();
}
