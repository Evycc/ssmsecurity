package com.evy.jing.controller;

import com.evy.jing.EnumMsg.EnumRole;
import com.evy.jing.EnumMsg.EnumSendMessage;
import com.evy.jing.model.*;
import com.evy.jing.pageplugin.PageModel;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.security.SaltUser;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserInfoService;
import com.evy.jing.service.SeUserRoleService;
import com.evy.jing.service.SeUserService;
import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.OtherUtils;
import com.evy.jing.websocket.MessagePushUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户操作控制器
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/user")
public class UserController {
    @Resource
    private SeUserService seUserService;
    @Resource
    private SeRoleService seRoleService;
    @Resource
    private SeUserRoleService seUserRoleService;
    @Resource
    private SeUserInfoService seUserInfoService;
    @Resource
    private JedisCache cache;
    @Resource
    private SessionRegistry sessionRegistry;
    @Resource
    private MessagePushUtil messagePushUtil;

    /**
     * 更新用户个人资料
     *
     * @param map k : username, k : email
     * @return
     */
    @RequestMapping(value = "/editUser", method = RequestMethod.POST)
    public ResponseEntity<List<String>> editUser(@RequestBody Map map) {

        String username = (String) map.get("username");
        String email = (String) map.get("email");

        if (username == null || "".equals(username)) {
            LoggerUtils.errorStr(getClass(), "用户名:%s 为空。", username);
            List<String> responseList = new ArrayList<String>();
            responseList.add("用户名为空");
            return new ResponseEntity<List<String>>(responseList, HttpStatus.CONFLICT);
        }
        if (email == null || "".equals(email)) {
            LoggerUtils.errorStr(getClass(), "电子邮箱:%s 为空。", email);
            List<String> responseList = new ArrayList<String>();
            responseList.add("电子邮箱为空");
            return new ResponseEntity<List<String>>(responseList, HttpStatus.CONFLICT);
        }

        List<SeUser> users = seUserService.findByUsername(username);
        if (users != null && users.size() > 0) {
            LoggerUtils.errorStr(getClass(), "该用户名已有人使用: %s 。", username);
            List<String> responseList = new ArrayList<String>();
            responseList.add("该用户名已有人使用");
            return new ResponseEntity<List<String>>(responseList, HttpStatus.CONFLICT);
        }

        List<SeUser> userList = seUserService.findByEmail(email);
        if (userList == null) {
            LoggerUtils.errorStr(getClass(), "获取不到用户: %s 个人资料。", email);
            List<String> responseList = new ArrayList<String>();
            responseList.add("获取不到该用户资料");
            return new ResponseEntity<List<String>>(responseList, HttpStatus.CONFLICT);
        }

        SeUser user = userList.get(0);
        user.setSsoId(username);
        user.setEmail(email);
        int updateRow = seUserService.update(user);
        LoggerUtils.debug(getClass(), "成功更新 %s 行。", updateRow);

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 获取当前登录用户username或email，及角色名
     *
     * @return 返回 0:username   1:role
     */
    @RequestMapping(value = "/getUserAndRole", method = RequestMethod.GET)
    public ResponseEntity<List<String>> getUserAndRole() {
        List<String> list = new ArrayList<String>();

        //当前登录用户名
        SaltUser user = (SaltUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        String username = user.getUsername();

        SeUser seUser = OtherUtils.returnUserByUsername(username);
        String ssoId = seUser.getSsoId();
        //默认返回用户名称
        if (ssoId != null && ssoId != "") {
            list.add(ssoId);
        } else {
            list.add(username);
        }
        //返回用户角色
        List<SeRole> seRoleList = seRoleService.findBySeUserId(seUser.getId());
        addRoleForList(list, seRoleList);

        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }

    /**
     * 获取用户个人资料（用户名，电子邮箱）
     *
     * @param username 用户名或邮箱
     * @return 0 : 用户名, 1 : 电子邮箱
     */
    @RequestMapping(value = "/getUserInfo", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getUserInfo(@RequestBody String username) {
        List<String> responseList = new ArrayList<String>();
        List<SeUser> userList = seUserService.findByUsername(username);
        if (username == null || "".equals(username)) {
            LoggerUtils.error(getClass(), "用户名为空。");
            return new ResponseEntity<List<String>>(HttpStatus.CONFLICT);
        }
        if (userList == null) {
            LoggerUtils.errorStr(getClass(), "获取不到用户: %s 个人资料。", username);
            return new ResponseEntity<List<String>>(HttpStatus.CONFLICT);
        }
        SeUser user = userList.get(0);
        responseList.add(user.getSsoId());
        responseList.add(user.getEmail());

        return new ResponseEntity<List<String>>(responseList, HttpStatus.OK);
    }

    /**
     * 向数组添加role角色名
     *
     * @param list    添加角色名到此数组
     * @param seRoles role角色数组
     */
    private void addRoleForList(List<String> list, List<SeRole> seRoles) {
        for (SeRole seRole : seRoles) {
            String role = seRole.getType();
            if (role.equals(EnumRole.USER.toString())) {
                list.add("用户");
            }
            if (role.equals(EnumRole.ROOT.toString())) {
                list.add("管理员");
            }
        }
    }

    /**
     * 返回用户头像id
     *
     * @param username
     * @return
     */
    @RequestMapping(value = "/headerId", method = RequestMethod.POST)
    public ResponseEntity<List<String>> getUserHeader(@RequestBody String username) {
        List<String> responseList = new ArrayList<String>();

        SeUser seUser = OtherUtils.returnUserByUsername(username);
        if (seUser == null) {
            LoggerUtils.errorStr(getClass(), "获取不到用户: %s 头像", username);
            return new ResponseEntity<List<String>>(HttpStatus.CONFLICT);
        }
        SeUserInfo userInfo = seUserInfoService.findByUserId(seUser.getId());

        if (userInfo == null) {
            LoggerUtils.errorStr(getClass(), "获取不到用户: %s 头像", username);
            return new ResponseEntity<List<String>>(HttpStatus.CONFLICT);
        }

        responseList.add(userInfo.getUserHeadId());

        return new ResponseEntity<List<String>>(responseList, HttpStatus.OK);
    }

    /**
     * 分页的形式返回所有SeUser信息
     *
     * @return k:PageModel 包含分页信息，v:分页获取的SeUser数组
     */
    @RequestMapping(value = "/getAllUser", method = RequestMethod.GET)
    public ResponseEntity<Map<PageModel, List<SeUser>>> getAllUser() {
        PageModel pageModel = new PageModel();
        List<SeUser> userList = seUserService.getAllUserByPage(pageModel);
        if (userList != null) {
            Map<PageModel, List<SeUser>> responseMap = new HashMap<PageModel, List<SeUser>>(3);
            responseMap.put(pageModel, userList);
            return new ResponseEntity<Map<PageModel, List<SeUser>>>(responseMap, HttpStatus.OK);
        }
        return new ResponseEntity<Map<PageModel, List<SeUser>>>(HttpStatus.NO_CONTENT);
    }

    /**
     * 分页的形式返回所有SeUser信息
     *
     * @param pageModel 包含分页信息
     * @return
     */
    @RequestMapping(value = "/getAllUserPage", method = RequestMethod.POST)
    public ResponseEntity<Map<PageModel, List<SeUser>>> getAllUserPage(@RequestBody PageModel pageModel) {
        System.out.println("pageModel = " + pageModel);
        List<SeUser> userList = seUserService.getAllUserByPage(pageModel);
        if (userList != null) {
            Map<PageModel, List<SeUser>> responseMap = new HashMap<PageModel, List<SeUser>>(3);
            responseMap.put(pageModel, userList);
            return new ResponseEntity<Map<PageModel, List<SeUser>>>(responseMap, HttpStatus.OK);
        }
        return new ResponseEntity<Map<PageModel, List<SeUser>>>(HttpStatus.NO_CONTENT);
    }

    /**
     * 根据用户主键锁定用户账号
     *
     * @param userId 用户主键
     * @return
     */
    @RequestMapping(value = "/lockedUser", method = RequestMethod.POST)
    public ResponseEntity<List<String>> lockedUser(@RequestBody Integer userId) {
        if (userId == null) {
            LoggerUtils.errorStr(getClass(), "锁定用户失败，用户主键为空");
            return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
        }
        try {
            int updateRow = seUserService.lockedUser(userId);
            LoggerUtils.debug(getClass(), "锁定账号数量:%s", updateRow);
        } catch (AccessDeniedException e) {
            return accessDeniedResponse(e);
        }
        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 根据用户主键解锁用户账号
     *
     * @param userId 用户主键
     * @return
     */
    @RequestMapping(value = "/unlockUser", method = RequestMethod.POST)
    public ResponseEntity<List<String>> unlockUser(@RequestBody Integer userId) {
        if (userId == null) {
            LoggerUtils.errorStr(getClass(), "解锁锁用户失败，用户主键为空");
            return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
        }
        try {
            int updateRow = seUserService.unlockUser(userId);
            LoggerUtils.debug(getClass(), "解锁账号数量:%s", updateRow);
        } catch (AccessDeniedException e) {
            return accessDeniedResponse(e);
        }

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 根据用户主键删除用户
     *
     * @param userId 用户主键
     * @return
     */
    @RequestMapping(value = "/deleteByUserId", method = RequestMethod.POST)
    public ResponseEntity<List<String>> deleteByUserId(@RequestBody Integer userId) {
        if (userId == null) {
            LoggerUtils.errorStr(getClass(), "删除用户失败，用户主键为空");
            return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
        }
        try {
            int deleteRow = seUserService.delete(userId);
            LoggerUtils.debug(getClass(), "删除账号数量:%s", deleteRow);
        } catch (AccessDeniedException e) {
            return accessDeniedResponse(e);
        }
        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 获取在线人数
     *
     * @return 返回在线人数
     */
    @RequestMapping(value = "/getOnlineNum", method = RequestMethod.GET)
    public ResponseEntity<Integer> getOnlineNum() {
        List<Object> userList = sessionRegistry.getAllPrincipals();
        int onlineNum = 0;

        if (userList == null || userList.isEmpty()) {
            return new ResponseEntity<Integer>(new Integer(onlineNum), HttpStatus.OK);
        }

        for (Object object : userList) {
            SaltUser saltUser = (SaltUser) object;

            /*start 判断用户是否在线*/
            List<SessionInformation> sessionInformations =
                    sessionRegistry.getAllSessions(saltUser, false);
            if (sessionInformations == null || sessionInformations.size() == 0) {
                //为注销状态不添加
                continue;
            }
            /*end 判断用户是否在线*/

            onlineNum++;
        }

        return new ResponseEntity<Integer>(onlineNum, HttpStatus.OK);
    }

    /**
     * 获取当前在线用户列表
     *
     * @return 返回用户数组
     */
    @RequestMapping(value = "/getOnlineUser", method = RequestMethod.GET)
    public ResponseEntity<List<SeUser>> getOnlineUser() {
        List<Object> onlineList = sessionRegistry.getAllPrincipals();
        List<SeUser> responseList = new ArrayList<SeUser>();

        if (onlineList == null || onlineList.isEmpty()) {
            return new ResponseEntity<List<SeUser>>(HttpStatus.NO_CONTENT);
        }
        for (Object object : onlineList) {
            SaltUser saltUser = (SaltUser) object;

            /*start 判断用户是否在线*/
            List<SessionInformation> sessionInformations =
                    sessionRegistry.getAllSessions(saltUser, false);
            if (sessionInformations == null || sessionInformations.size() == 0) {
                //为注销状态不添加
                continue;
            }
            /*end 判断用户是否在线*/

            SeUser user = OtherUtils.returnUserByUsername(saltUser.getUsername());
            responseList.add(user);
        }
        return new ResponseEntity<List<SeUser>>(responseList, HttpStatus.OK);
    }

    /**
     * 获取全部用户及用户对应角色名
     *
     * @return k: 用户电子邮箱 v: 对应角色数组
     */
    @RequestMapping(value = "/getRoleManage", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<SeRole>>> getRoleManage() {
        Map<String, List<SeRole>> responseMap =
                new HashMap<String, List<SeRole>>(10);

        List<SeUser> userList = seUserService.findAllUser();
        List<SeRole> roleList = seRoleService.findAllRole();
        List<SeUserRole> userRoleList = seUserRoleService.findAll();

        //包装responseMap
        for (SeUser user : userList) {
            List<SeRole> roles = new ArrayList<SeRole>();
            for (SeUserRole userRole : userRoleList) {
                if (user.getId().equals(userRole.getUserId())) {
                    for (SeRole role : roleList) {
                        if (role.getId().equals(userRole.getRoleId())) {
                            roles.add(role);
                        }
                    }
                }
            }
            if (!roles.isEmpty()) {
                responseMap.put(user.getEmail(), roles);
            }
        }
        return new ResponseEntity<Map<String, List<SeRole>>>(responseMap, HttpStatus.OK);
    }

    /**
     * 获取所有角色名
     *
     * @return 返回所有角色数组
     */
    @RequestMapping(value = "/getAllRole", method = RequestMethod.GET)
    public ResponseEntity<List<SeRole>> getAllRoleObj() {
        List<SeRole> roleList = seRoleService.findAllRole();
        return new ResponseEntity<List<SeRole>>(roleList, HttpStatus.OK);
    }

    /**
     * 为用户添加角色
     *
     * @param map k:username  用户名   k:roles  角色名的数组
     * @return
     */
    @RequestMapping(value = "/addRoleByUsername", method = RequestMethod.POST)
    public ResponseEntity<List<String>> addRoleByUsername(@RequestBody Map map) {
        if (map == null || map.isEmpty()) {
            LoggerUtils.errorStr(getClass(), "为用户添加角色失败，参数数组为空。");
            return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
        }
        String username = (String) map.get("username");
        List<String> roles = (List<String>) map.get("roles");

        SeUser user = OtherUtils.returnUserByUsername(username);
        List<SeUserRole> userRoleList = OtherUtils.selectAllUserRoleByAdd(roles, user);

        if (userRoleList == null) {
            LoggerUtils.errorStr(getClass(), "未执行添加角色操作，用户已存在相关角色。");
            return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
        }

        try {
            int insertNum = seUserRoleService.insertList(userRoleList);
            LoggerUtils.debug(getClass(), "添加角色成功，新增数据库%s行。", insertNum);
        } catch (AccessDeniedException e) {
            return accessDeniedResponse(e);
        }

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 删除用户角色
     *
     * @param map
     * @return
     */
    @RequestMapping(value = "/delRoleByUsername", method = RequestMethod.POST)
    public ResponseEntity<List<String>> deleteRoleByUsername(@RequestBody Map map) {
        if (map == null || map.isEmpty()) {
            LoggerUtils.errorStr(getClass(), "删除角色失败，参数数组为空。");
            return new ResponseEntity<List<String>>(HttpStatus.PRECONDITION_FAILED);
        }

        String username = (String) map.get("username");
        List<String> roles = (List<String>) map.get("roles");

        SeUser user = OtherUtils.returnUserByUsername(username);
        List<SeUserRole> userRoleList = OtherUtils.selectAllUserRoleByDel(roles, user);

        if (userRoleList == null) {
            LoggerUtils.errorStr(getClass(), "未执行删除角色操作，用户不存在相关角色。");
            return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
        }

        try {
            int deleteRow = seUserRoleService.deleteList(user.getId(), userRoleList);
            LoggerUtils.debug(getClass(), "删除角色成功，新增数据库%s行。", deleteRow);
        } catch (AccessDeniedException e) {
            return accessDeniedResponse(e);
        }

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 踢出指定用户
     *
     * @param email 用户电子邮箱
     * @return
     */
    @RequestMapping(value = "/kickoutByEmail", method = RequestMethod.POST)
    public ResponseEntity<Void> kickoutByEmail(@RequestBody String email) {
        if (email == null) {
            return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
        }
        OtherUtils.kickoutByUsername(email);
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * 查询用户是否在线
     *
     * @param username 用户名
     * @return 在线返回true，反之返回false
     */
    @RequestMapping(value = "/isOnlineUsername", method = RequestMethod.POST)
    public ResponseEntity<Boolean> isOnlineByUsername(@RequestBody String username) {
        boolean returnBoolean = false;
        if (OtherUtils.isOnlineByUsername(username) != null) {
            returnBoolean = true;
        }
        return new ResponseEntity<Boolean>(returnBoolean, HttpStatus.OK);
    }

    /**
     * 单点发送
     *
     * @param messageMap from:发信人    to:收信人  message:消息正文
     * @return 发送完成返回true
     */
    @RequestMapping(value = "/ws/sendToUser", method = RequestMethod.POST)
    public ResponseEntity<Boolean> sendToUser(@RequestBody Map messageMap) {
        MessageModel model = new MessageModel();
        String from = EnumSendMessage.FROM.getReasonPhrase();
        String to = EnumSendMessage.TO.getReasonPhrase();
        String message = EnumSendMessage.MESSAGE.getReasonPhrase();

        if (messageMap.containsKey(from)) {
            //以邮箱作为用户标识
            from = (String) messageMap.get(from);
            from = OtherUtils.returnUserByUsername(from).getEmail();
            model.setFrom(from);
        }
        if (messageMap.containsKey(to)) {
            model.setTo((String) messageMap.get(to));
        }
        if (messageMap.containsKey(message)) {
            model.setMessage(String.valueOf(messageMap.get(message)));
        }

        return new ResponseEntity<Boolean>(messagePushUtil.sendToUser(model), HttpStatus.OK);
    }

    /**
     * 查询并返回用户的离线消息
     *
     * @param map 发信人及收信人   from:接收离线信息用户    to:发送离线信息用户
     * @return
     */
    @RequestMapping(value = "/ws/getOfflineMessage", method = RequestMethod.POST)
    public ResponseEntity<Boolean> getOfflineMessage(@RequestBody Map map) {
        String from = EnumSendMessage.FROM.getReasonPhrase();
        String to = EnumSendMessage.TO.getReasonPhrase();

        if (!map.containsKey(from) || !map.containsKey(to)) {
            LoggerUtils.error(getClass(), "from 或 to不存在!");
            return new ResponseEntity<Boolean>(HttpStatus.NO_CONTENT);
        }
        from = (String) map.get(from);
        to = (String) map.get(to);

        //以邮箱作为用户标识
        from = OtherUtils.returnUserByUsername(from).getEmail();

        //默认发送离线信息
        boolean isSuccess = messagePushUtil.sendOfflineMessage(from, to);
        if (!isSuccess){
            //不存在离线信息，返回全部对话记录
            isSuccess = messagePushUtil.getMessageRecord(from, to);
        }

        return new ResponseEntity<Boolean>(isSuccess, HttpStatus.OK);
    }

    /**
     * 权限不足处理
     *
     * @param e AccessDeniedException异常
     * @return 返回405错误码及错误提示JSON形式
     */
    private ResponseEntity<List<String>> accessDeniedResponse(AccessDeniedException e) {
        List<String> list = new ArrayList<String>(1);
        list.add("权限不足");
        return new ResponseEntity<List<String>>(list, HttpStatus.METHOD_NOT_ALLOWED);
    }
}
