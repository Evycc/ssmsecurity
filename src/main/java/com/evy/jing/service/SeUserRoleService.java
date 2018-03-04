package com.evy.jing.service;

import com.evy.jing.model.SeUserRole;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SeUserRoleService extends BaseService<SeUserRole, Integer> {
    /**
     * 根据userID查找SeUserRole
     * @param userId
     * @return  找不到返回null
     */
    List<SeUserRole> findByUserId(Integer userId);

    /**
     * 查找全部用户-角色表
     * @return  全部用户-角色
     */
    List<SeUserRole> findAll();

    /**
     * 根据用户主键删除
     * @return
     */
    int deleteByUserId(Integer userId);

    /**
     * 插入多条记录
     * @param userRoles SeUserRole数组
     * @return
     */
    @PreAuthorize("hasRole('ROOT')")
    int insertList(List<SeUserRole> userRoles);

    /**
     * 根据用户主键与角色主键查找
     * @param userId    用户主键
     * @param roleId    角色主键
     * @return  符合的SeUserRole数组，为空返回null
     */
    List<SeUserRole> selectByUserIdRoleId(Integer userId, Integer roleId);

    /**
     * 批量删除
     * @param userRoles SeUserRole数组
     * @return
     */
    @PreAuthorize("hasRole('ROOT')")
    int deleteList(Integer userId, List<SeUserRole> userRoles);
}
