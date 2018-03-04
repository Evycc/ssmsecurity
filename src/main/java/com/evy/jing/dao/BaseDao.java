package com.evy.jing.dao;

/**
 * 自定义DAO接口，定义CRUD方法
 * @param <Model>   实体类
 * @param <PK>  主键
 */
public interface BaseDao<Model,PK> {
    /**
     * 插入对象
     * @param model 用户类
     * @return  返回插入的数据库行数
     */
    int insert(Model model);

    /**
     * 更新对象
     * @param model 用户类
     * @return  返回更新的数据库行数
     */
    int update(Model model);

    /**
     * 通过主键删除对象
     * @param id    用户主键
     * @return  返回删除的数据库行数
     */
    int delete(PK id);

    /**
     * 根据主键查找
     * @param id    用户主键
     * @return  为空返回null
     */
    Model selectByPrimaryKey(PK id);
}
