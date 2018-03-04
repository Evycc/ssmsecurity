package com.evy.jing.mapper;

import com.evy.jing.model.SeUserInfo;
import com.evy.jing.model.SeUserInfoExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeUserInfoMapper {
    long countByExample(SeUserInfoExample example);

    int deleteByExample(SeUserInfoExample example);

    int insert(SeUserInfo record);

    int insertSelective(SeUserInfo record);

    List<SeUserInfo> selectByExample(SeUserInfoExample example);

    int updateByExampleSelective(@Param("record") SeUserInfo record, @Param("example") SeUserInfoExample example);

    int updateByExample(@Param("record") SeUserInfo record, @Param("example") SeUserInfoExample example);
}