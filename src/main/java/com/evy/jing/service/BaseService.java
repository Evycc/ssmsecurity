package com.evy.jing.service;

import org.springframework.security.access.prepost.PreAuthorize;

/**
 *  自定义Service接口，封装常用CRUD方法
 * @param <Model>   实体类
 * @param <PK>  主键
 */
public interface BaseService<Model, PK> {
    /**
     * 插入对象
     * @param model 用户类
     * @return  返回数据库插入行数
     */
    int insert(Model model);

    /**
     * 更新对象
     * @param model 用户类
     * @return  返回数据库更新行数
     */
    int update(Model model);

    /**
     * 通过主键删除对象
     * @param id    用户主键
     * @return  返回数据库删除行数
     */
    @PreAuthorize("hasRole('ROOT')")
    int delete(PK id);

    /**
     * 根据主键查找
     * @param id    用户主键
     * @return  查找不到返回null
     */
    Model selectByPrimaryKey(PK id);
}
