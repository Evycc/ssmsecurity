package com.evy.jing.mapper;

import com.evy.jing.model.SeUser;
import com.evy.jing.model.SeUserExample;
import com.evy.jing.pageplugin.PageModel;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SeUserMapper {
    long countByExample(SeUserExample example);

    int deleteByExample(SeUserExample example);

    int insert(SeUser record);

    int insertSelective(SeUser record);

    List<SeUser> selectByExample(SeUserExample example);

    int updateByExampleSelective(@Param("record") SeUser record, @Param("example") SeUserExample example);

    int updateByExample(@Param("record") SeUser record, @Param("example") SeUserExample example);

    /**
     * 分页获取所有SeUser数据
     * @param pageModel 分页类
     * @return  SeuUser对象数组
     */
    List<SeUser> selectListPage(@Param("pageModel") PageModel pageModel);
}