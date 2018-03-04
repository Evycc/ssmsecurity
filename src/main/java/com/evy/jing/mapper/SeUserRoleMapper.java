package com.evy.jing.mapper;

import com.evy.jing.model.SeUserRole;
import com.evy.jing.model.SeUserRoleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeUserRoleMapper {
    long countByExample(SeUserRoleExample example);

    int deleteByExample(SeUserRoleExample example);

    int insert(SeUserRole record);

    int insertSelective(SeUserRole record);

    List<SeUserRole> selectByExample(SeUserRoleExample example);

    int updateByExampleSelective(@Param("record") SeUserRole record, @Param("example") SeUserRoleExample example);

    int updateByExample(@Param("record") SeUserRole record, @Param("example") SeUserRoleExample example);

    int insertByList(List<SeUserRole> userRoles);

    int deleteByList(@Param("userId") Integer userId, @Param("userRoles") List<SeUserRole> userRoles);
}