package com.evy.jing.controller;

import com.evy.jing.EnumMsg.EnumLoginError;
import com.evy.jing.EnumMsg.EnumRole;
import com.evy.jing.model.*;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.security.SaltUser;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserInfoService;
import com.evy.jing.service.SeUserRoleService;
import com.evy.jing.service.SeUserService;
import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.OtherUtils;
import com.evy.jing.util.SendEmail;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.List;

/**
 * 其他操作
 */
@RestController
public class UtilController {
    @Resource
    SeUserInfoService seUserInfoService;
    @Resource
    SeUserService seUserService;
    @Resource
    JedisCache cache;
    @Resource
    SeRoleService seRoleService;
    @Resource
    SeUserRoleService seUserRoleService;

    /**
     * 获取并返回session的最大过期时间
     * @param session   获取的session
     * @return  返回过期时间
     */
    @RequestMapping(value = "/getSessionInterval", method = RequestMethod.POST)
    public ResponseEntity<List<Integer>> getSessionInterval(HttpSession session){
        List<Integer> list = new ArrayList<Integer>(1);
        list.add(session.getMaxInactiveInterval());

        return new ResponseEntity<List<Integer>>(list, HttpStatus.OK);
    }

    //距离上次请求的间隔
    private static long lastInterval = 0;
    @RequestMapping(value = "/sessionExpireInterval", method = RequestMethod.POST)
    public ResponseEntity<List<Integer>> SessionExpireInterval(HttpSession session){
        List<Integer> list = new ArrayList<Integer>(1);
        long accessTime = session.getLastAccessedTime();
        long now = System.currentTimeMillis();
        long t = (now - accessTime) / 1000;
        int s = session.getMaxInactiveInterval();

        if (t + lastInterval >= s){
            //session过期
            list.add(-1);
        } else {
            //返回剩余过期时间
            lastInterval = t;
            list.add((int) (s - t));
        }
        return new ResponseEntity<List<Integer>>(list, HttpStatus.OK);
    }

    /**
     * 当前用户是否登录
     * @return  存在返回用户email或username，反之返回空字符串
     */
    @RequestMapping(value = "/hasLoginUser", method = RequestMethod.GET)
    public ResponseEntity<List<String>> hasLoginUser(){
        List<String> list = new ArrayList<String>();
        Object object = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();

        if (object instanceof String){
            //游客anonymousUser
        }
        if (object instanceof SaltUser){
            SaltUser user = (SaltUser) object;
            list.add(user.getUsername());
        }

        return new ResponseEntity<List<String>>(list, HttpStatus.OK);
    }

    /**
     * 上传文件
     * @param file  上传的图片
     * @param rect  上传图片裁剪坐标    'x: y: w: h:'格式
     * @return  上传成功返回success状态
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseEntity<List<String>> uploadFile(@RequestParam(value = "logo") MultipartFile file,
                                                   @RequestParam(value = "rect") String rect,
                                                   @RequestParam(value = "username") String username,
                                                   HttpServletRequest request) throws IOException {

        List<String> list = new ArrayList<String>();

        //存放到服务器下文件路径
        final String UPLOAD_URL = request.getSession().getServletContext()
                .getRealPath("/WEB-INF/static/png/");

        //随机文件名
        UUID FileId = UUID.randomUUID();

        //上传文件大小不能超过1M
        if (file == null || file.getSize() > 1048576L){
            list.add("上传文件不能为空。");
            return new ResponseEntity<List<String>>(list, HttpStatus.CONFLICT);
        }

        int index = file.getOriginalFilename().lastIndexOf(".");
        //获取文件后缀
        String fileSuffix = file.getOriginalFilename().substring(index);
        String suffix = fileSuffix.substring(fileSuffix.indexOf(".")+1);

        //只支持image格式
        //ImageIO支持文件类型[JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
        String types = Arrays.toString(ImageIO.getReaderFormatNames());
        if (suffix == null ||types.indexOf(suffix) < 0 ){
            LoggerUtils.errorStr(OtherUtils.class,  "file : %s 格式有误，只支持图片格式文件",
                    file.getOriginalFilename());
            list.add("只支持图片格式文件。");
            return new ResponseEntity<List<String>>(list, HttpStatus.CONFLICT);
        }

        File newFile = new File(UPLOAD_URL + FileId + fileSuffix);

        //获取裁剪坐标
        int indexX = rect.indexOf("x:");
        int indexY = rect.indexOf("y:");
        int indexW = rect.indexOf("w:");
        int indexH = rect.indexOf("h:");
        String x = rect.substring(indexX + 2, indexY);
        String y = rect.substring(indexY + 2, indexW);
        String w = rect.substring(indexW + 2, indexH);
        String h = rect.substring(indexH + 2);

        //去掉小数点
        String regexp = ".";
        if (x.lastIndexOf(regexp) > -1){
            x = x.substring(0, x.lastIndexOf("."));
        }
        if (y.lastIndexOf(regexp) > -1){
            y = y.substring(0, y.lastIndexOf("."));
        }
        if (w.lastIndexOf(regexp) > -1){
            w = w.substring(0, w.lastIndexOf("."));
        }
        if (h.lastIndexOf(regexp) > -1){
            h = h.substring(0, h.lastIndexOf("."));
        }

        //将获取到的坐标转换为int格式
        int obj_x = Integer.parseInt(x.trim());
        int obj_y = Integer.parseInt(y.trim());
        int obj_w = Integer.parseInt(w.trim());
        int obj_h = Integer.parseInt(h.trim());

        LoggerUtils.debug(getClass(), "裁剪坐标: (%s,%s,%s,%s)", x, y, w, h);

        //保存用户上传图片到服务器
        if (obj_x == 0 && obj_y == 0 && obj_w == 0 && obj_h == 0){
            FileCopyUtils.copy(file.getBytes(), newFile);
        }else {
            OtherUtils.CutoutImage(file.getInputStream(), new Rectangle(obj_x,
                    obj_y,obj_w,obj_h),newFile);
        }

        //更新用户信息表
        List<SeUser> userList = seUserService.findByUsername(username);
        SeUser seUser;
        if (userList != null) {
            seUser = userList.get(0);
        }else {
            LoggerUtils.error(getClass(), "查找不到该用户信息: /s 更改头像失败");
            return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
        }
        SeUserInfo seUserInfo = seUserInfoService.findByUserId(seUser.getId());
        seUserInfo.setUserHeadId(FileId + fileSuffix);
        seUserInfoService.update(seUserInfo);

        return new ResponseEntity<List<String>>(HttpStatus.OK);
    }

    /**
     * 重置密码
     * @param map k:uuid  k:email_guid  k:password  k:browser  k:os
     * @return 返回true则更新成功，返回false则密码与现密码一样
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.POST)
    public ResponseEntity<List<Boolean>> resetPassword(@RequestBody Map map, HttpServletRequest request){
        List<Boolean> responseList = new ArrayList<Boolean>();

        String uuid = (String) map.get("uuid");
        String email_guid = (String) map.get("email_guid");
        String newPassword = (String) map.get("password");

        String uuidStr = (String) cache.get(uuid);
        if (uuidStr == null){
            LoggerUtils.errorStr(getClass(), "查找不到uuid: %s", uuid);
            return new ResponseEntity<List<Boolean>>(HttpStatus.NO_CONTENT);
        }
        if (!email_guid.equals(uuidStr)) {
            LoggerUtils.errorStr(getClass(), "%s 不存在对应email_guid", email_guid);
            return new ResponseEntity<List<Boolean>>(HttpStatus.NO_CONTENT);
        }
        //获取该email_guid对应用户电子邮箱
        String email = (String) cache.get(email_guid);
        if (email == null){
            LoggerUtils.errorStr(getClass(), "%s对应email为空", email_guid);
            return new ResponseEntity<List<Boolean>>(HttpStatus.NO_CONTENT);
        }
        List<SeUser> userList = seUserService.findByEmail(email);
        if (userList == null){
            LoggerUtils.errorStr(getClass(), "%s 不存在对应电子邮箱", email_guid);
            return new ResponseEntity<List<Boolean>>(HttpStatus.NO_CONTENT);
        }
        SeUser user = userList.get(0);

        //与现密码是否相同
        if (seUserService.isEqualsPassword(user, newPassword)){
            LoggerUtils.errorStr(getClass(), "%s 与现密码相同。", newPassword);
            responseList.add(Boolean.FALSE);
            return new ResponseEntity<List<Boolean>>(responseList, HttpStatus.OK);
        }

        user.setPassword(newPassword);
        int updateRow = seUserService.updateUserPassword(user);
        LoggerUtils.info(getClass(), "重置用户密码成功，成功更新的数据库行数:%s", updateRow);

        //清除缓存
        cache.remove(uuid);
        cache.remove(email_guid);

        //发送重置密码成功邮件
        map.put("user_ip", OtherUtils.getUserIP(request));
        map.put("email", email);
        SendEmail.sendChangePasswordSuccess(map);

        //踢出用户，要求重新登录
        OtherUtils.kickoutByUsername(email);
        cache.put(EnumLoginError.CHANGE_PASSWORD, "密码已更改，请重新登录。");

        responseList.add(Boolean.TRUE);
        return new ResponseEntity<List<Boolean>>(responseList, HttpStatus.OK);
    }

    /**
     * 查询该uuid是否失效
     * @param uuid
     * @return  返回true则失效，反之有效并返回存储邮箱
     */
    @RequestMapping(value = "/isExpireUUID", method = RequestMethod.POST)
    public ResponseEntity<List<String>> isExpireUUID(@RequestBody String uuid){
        List<String> responseList = new ArrayList<String>();

        if (uuid == null){
            LoggerUtils.error(getClass(), "uuid为空");
            return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
        }
        String get_uuid = (String) cache.get(uuid);
        if (get_uuid != null) {
            String email = (String) cache.get(get_uuid);
            if (email == null){
                LoggerUtils.errorStr(getClass(), "uuid:%s 对应邮箱为空", uuid);
                return new ResponseEntity<List<String>>(HttpStatus.NO_CONTENT);
            }

            responseList.add(Boolean.FALSE.toString());
            responseList.add(email);
            return new ResponseEntity<List<String>>(responseList, HttpStatus.OK);
        }
        responseList.add(Boolean.TRUE.toString());
        return new ResponseEntity<List<String>>(responseList, HttpStatus.OK);
    }

    /**
     * 向申请修改密码的用户发送一封修改密码的电子邮件
     * @param email 用户名或电子邮箱
     * @return
     */
    @RequestMapping(value = "/editPassword", method = RequestMethod.POST)
    public ResponseEntity<Void> sendEmailEditPassword(@RequestBody String email){
        SeUser user = OtherUtils.returnUserByUsername(email);
        SendEmail.sendEditPassword(user.getEmail());

        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    /**
     * 注册用户
     * @param map   k:email k:password
     * @return
     */
    @RequestMapping(value = "createUser", method = RequestMethod.POST)
    public ResponseEntity<Void> create(@RequestBody Map map){

        String email = (String) map.get("email");
        String password = (String) map.get("password");

        SeUser user = new SeUser(email, password);
        int success = seUserService.insert(user);

        if (success > 0){

            //注册成功，添加一般用户角色
            SeUserRole seUserRole = new SeUserRole();
            List<SeUser> seUser = seUserService.findByEmail(email);
            SeRole seRole = seRoleService.findByType(EnumRole.USER.toString());

            if (seUser != null && seRole != null){
                Integer userId = seUser.get(0).getId();
                Integer roleId = seRole.getId();

                seUserRole.setUserId(userId);
                seUserRole.setRoleId(roleId);

                seUserRoleService.insert(seUserRole);
            }

            //注册成功，添加默认角色信息(头像)
            SeUserInfo seUserInfo = new SeUserInfo();
            seUserInfo.setUserId(seUser.get(0).getId());
            seUserInfoService.insert(seUserInfo);

            return new ResponseEntity<Void>(HttpStatus.CREATED);
        }

        return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
