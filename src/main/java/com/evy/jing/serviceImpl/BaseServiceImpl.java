package com.evy.jing.serviceImpl;

import com.evy.jing.dao.BaseDao;
import com.evy.jing.service.BaseService;

/**
 * 常用CURD实现类
 * @param <Model>
 * @param <PK>
 */
public abstract class BaseServiceImpl<Model, PK> implements BaseService<Model, PK> {
    /**
     * 抽象方法
     * @return  DAO实现类
     */
    abstract BaseDao<Model, PK> getDao();

    @Override
    public int insert(Model model) {
        return getDao().insert(model);
    }

    @Override
    public int update(Model model) {
        return getDao().update(model);
    }

    @Override
    public int delete(PK id) {
        return getDao().delete(id);
    }

    @Override
    public Model selectByPrimaryKey(PK id) {
        return getDao().selectByPrimaryKey(id);
    }
}
