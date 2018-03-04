package com.evy.jing.dao;

import com.evy.jing.model.SeUserRole;

import java.util.List;

public interface SeUserRoleDao extends BaseDao<SeUserRole, Integer> {
    /**
     * 根据用户id查找UserRole
     * @param id    用户主键
     * @return  找不到返回null
     */
    List<SeUserRole> findBySeUserId(Integer id);

    /**
     * 查找全部用户-角色表
     * @return  全部用户-角色
     */
    List<SeUserRole> findAllUserRole();

    /**
     * 根据用户主键删除
     * @return
     */
    int deleteByUserId(Integer userId);

    /**
     * 批量插入
     * @param userRoles SeUserRole数组
     * @return
     */
    int insertByUserRoleList(List<SeUserRole> userRoles);

    /**
     * 根据用户主键与角色主键查找
     * @param userId    用户主键
     * @param roleId    角色主键
     * @return  符合的SeUserRole数组，为空返回null
     */
    List<SeUserRole> selectByUserIdAndRoleId(Integer userId, Integer roleId);

    /**
     * 批量删除
     * @param userId 用户主键
     * @param userRoles SeUserRole数组
     * @return
     */
    int delteList(Integer userId, List<SeUserRole> userRoles);
}
