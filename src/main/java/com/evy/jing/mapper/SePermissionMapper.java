package com.evy.jing.mapper;

import com.evy.jing.model.SePermission;
import com.evy.jing.model.SePermissionExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SePermissionMapper {
    long countByExample(SePermissionExample example);

    int deleteByExample(SePermissionExample example);

    int insert(SePermission record);

    int insertSelective(SePermission record);

    List<SePermission> selectByExample(SePermissionExample example);

    int updateByExampleSelective(@Param("record") SePermission record, @Param("example") SePermissionExample example);

    int updateByExample(@Param("record") SePermission record, @Param("example") SePermissionExample example);
}