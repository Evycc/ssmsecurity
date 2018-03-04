package com.evy.jing.mapper;

import com.evy.jing.model.SeRolePermission;
import com.evy.jing.model.SeRolePermissionExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeRolePermissionMapper {
    long countByExample(SeRolePermissionExample example);

    int deleteByExample(SeRolePermissionExample example);

    int insert(SeRolePermission record);

    int insertSelective(SeRolePermission record);

    List<SeRolePermission> selectByExample(SeRolePermissionExample example);

    int updateByExampleSelective(@Param("record") SeRolePermission record, @Param("example") SeRolePermissionExample example);

    int updateByExample(@Param("record") SeRolePermission record, @Param("example") SeRolePermissionExample example);
}