package com.evy.jing.dao;

import com.evy.jing.model.SeRole;

import java.util.List;

public interface SeRoleDao extends BaseDao<SeRole, Integer> {
    /**
     * 根据SeUser主键查找对应角色
     * @param id
     * @return
     */
    List<SeRole> findBySeUserId(Integer id);

    /**
     * 根据角色名查找SeRole
     * @param type
     * @return  找不到返回null
     */
    SeRole findByType(String type);

    /**
     * 获取全部角色
     * @return  全部角色数组
     */
    List<SeRole> getAllRole();
}
