package com.evy.jing.mapper;

import com.evy.jing.model.SeRole;
import com.evy.jing.model.SeRoleExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeRoleMapper {
    long countByExample(SeRoleExample example);

    int deleteByExample(SeRoleExample example);

    int insert(SeRole record);

    int insertSelective(SeRole record);

    List<SeRole> selectByExample(SeRoleExample example);

    int updateByExampleSelective(@Param("record") SeRole record, @Param("example") SeRoleExample example);

    int updateByExample(@Param("record") SeRole record, @Param("example") SeRoleExample example);
}