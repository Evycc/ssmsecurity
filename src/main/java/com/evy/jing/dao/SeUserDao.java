package com.evy.jing.dao;

import com.evy.jing.model.SeUser;
import com.evy.jing.pageplugin.PageModel;

import java.util.List;

public interface SeUserDao extends BaseDao<SeUser, Integer> {
    /**
     * 根据用户名查找
     * @param username  用户名
     * @return  查找不到返回null
     */
    List<SeUser> findByUsername(String username);
    /**
     * 根据邮箱查找
     * @param email 用户电子邮箱
     * @return  查找不到返回null
     */
    List<SeUser> findByEmail(String email);

    /**
     * 分页获取全部SeUser数据
     * @param pageModel 包含分页信息的类
     * @return  SeUser对象数组，为空则返回null
     */
    List<SeUser> getAllByPage(PageModel pageModel);

    /**
     * 根据用户主键锁定用户
     * @param userId    用户主键
     * @return  返回数据库更新行数
     */
    int lockedByUserId(Integer userId);

    /**
     * 解锁用户账号
     * @param userId    用户主键
     * @return  返回数据库更新行数
     */
    int unlockUserId(Integer userId);

    /**
     * 获取全部用户
     * @return  返回全部用户列表
     */
    List<SeUser> getAllUser();
}
