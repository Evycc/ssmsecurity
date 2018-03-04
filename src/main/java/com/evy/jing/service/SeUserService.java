package com.evy.jing.service;

import com.evy.jing.model.SeUser;
import com.evy.jing.pageplugin.PageModel;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

public interface SeUserService extends BaseService<SeUser, Integer> {
    /**
     * 根据用户名查找用户
     * @param username
     * @return 查找不到返回null
     */
    List<SeUser> findByUsername(String username);
    /**
     * 根据邮箱查找用户
     * @param email
     * @return  查找不到返回null
     */
    List<SeUser> findByEmail(String email);

    /**
     * 查找是否存在该username
     * @param username
     * @return  存在返回true
     */
    boolean isExistUsername(String username);

    /**
     * 查找是否存在该email
     * @param email
     * @return  存在返回true
     */
    boolean isExistEmail(String email);

    /**
     * 更新重置用户密码
     * @param user  设置了新密码的SeUser
     * @return
     */
    int updateUserPassword(SeUser user);

    /**
     * 判断两密码是否相同
     * @param user  包含密码与盐的SeUser
     * @param newPassword   对比的密码
     * @return  true则两密码v，反之不相同
     */
    boolean isEqualsPassword(SeUser user, String newPassword);

    /**
     * 分页获取全部SeUser对象
     * @param pageModel 包含分页数据的包装类
     * @return  返回分页之后的SeUser对象数组，为空则返回null
     */
    List<SeUser> getAllUserByPage(PageModel pageModel);

    /**
     * 根据主键锁定用户账号
     * @param id    用户主键
     * @return  返回数据库更新行数
     */
    @PreAuthorize("hasRole('ROOT')")
    int lockedUser(Integer id);

    /**
     * 解锁用户账号
     * @param id    用户主键
     * @return  返回数据库更新行数
     */
    @PreAuthorize("hasRole('ROOT')")
    int unlockUser(Integer id);

    /**
     * 获取全部用户
     * @return  全部用户数组
     */
    List<SeUser> findAllUser();
}
