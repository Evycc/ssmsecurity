package com.evy.jing.util;

import com.evy.jing.model.SeRole;
import com.evy.jing.model.SeUser;
import com.evy.jing.model.SeUserRole;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.security.SaltUser;
import com.evy.jing.service.SeRoleService;
import com.evy.jing.service.SeUserRoleService;
import com.evy.jing.service.SeUserService;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 工具类
 */
@Component
public class OtherUtils {
    private static SessionRegistry sessionRegistry;
    private static SeUserService seUserService;
    private static SeRoleService seRoleService;
    private static SeUserRoleService seUserRoleService;
    private static JedisCache cache;

    @PostConstruct
    private void init() {
        if (sessionRegistry == null) {
            sessionRegistry = FactoryStaticResource.getSessionRegistry();
        }
        if (seUserService == null) {
            seUserService = FactoryStaticResource.getSeUserService();
        }
        if (cache == null) {
            cache = FactoryStaticResource.getCache();
        }
        if (seRoleService == null) {
            seRoleService = FactoryStaticResource.getSeRoleService();
        }
        if (seUserRoleService == null) {
            seUserRoleService = FactoryStaticResource.getSeUserRoleService();
        }
    }

    /**
     * 截取图片
     *
     * @param fileImage  截取图片
     * @param rectangle  截取图片的矩形区域
     * @param returnFile 写入该文件
     * @return 截取完成的图片
     */
    public static File CutoutImage(File fileImage, Rectangle rectangle, File returnFile) {
        FileInputStream fileInputStream = null;
        ImageInputStream imageInputStream = null;
        if (fileImage.exists()) {
            //判断文件是否图片类型
            //ImageIO支持文件类型[JPG, jpg, bmp, BMP, gif, GIF, WBMP, png, PNG, wbmp, jpeg, JPEG]
            String types = Arrays.toString(ImageIO.getReaderFormatNames());
            String suffix = null;
            if (fileImage.getName().lastIndexOf(".") > -1) {
                //获取文件后缀名
                suffix = fileImage.getName().substring(fileImage.getName().lastIndexOf(".") + 1);
            }
            if (suffix == null || types.indexOf(suffix) < 0) {
                LoggerUtils.errorStr(OtherUtils.class, "file : %s 格式有误，只支持图片格式文件",
                        fileImage.getName());
                return null;
            }
            try {
                fileInputStream = new FileInputStream(fileImage);
                imageInputStream = ImageIO.createImageInputStream(fileInputStream);

                //根据图片类型获取该类型的解析解码图像类   ImageReader
                ImageReader reader = ImageIO.getImageReadersBySuffix(suffix).next();
                reader.setInput(imageInputStream);
                ImageReadParam param = reader.getDefaultReadParam();
                //设置图片截取矩形区域
                param.setSourceRegion(rectangle);
                BufferedImage bufferedImage = reader.read(0, param);

                ImageIO.write(bufferedImage, suffix, returnFile);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                closeInputStream(fileInputStream, imageInputStream);
            }
        }
        return returnFile;
    }

    /**
     * 截取图片
     *
     * @param fileInput  图片的输入流
     * @param rectangle  截取图片的矩形区域
     * @param returnFile 写入该文件
     * @return 裁剪完成的图片
     */
    public static File CutoutImage(InputStream fileInput, Rectangle rectangle, File returnFile) {
        FileInputStream fileInputStream = (FileInputStream) fileInput;
        ImageInputStream imageInputStream = null;

        try {
            imageInputStream = ImageIO.createImageInputStream(fileInputStream);

            //根据图片类型获取该类型的解析解码图像类   ImageReader
            ImageReader reader = ImageIO.getImageReaders(imageInputStream).next();
            reader.setInput(imageInputStream);
            ImageReadParam param = reader.getDefaultReadParam();
            //设置图片截取矩形区域
            param.setSourceRegion(rectangle);
            BufferedImage bufferedImage = reader.read(0, param);

            ImageIO.write(bufferedImage, reader.getFormatName(), returnFile);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeInputStream(fileInputStream, imageInputStream);
        }
        return returnFile;
    }

    /**
     * 关闭输入流
     *
     * @param fileInputStream
     * @param imageInputStream
     */
    private static void closeInputStream(FileInputStream fileInputStream,
                                         ImageInputStream imageInputStream) {
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (imageInputStream != null) {
                imageInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 返回请求ip
     *
     * @param request
     * @return
     */
    public static String getUserIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        String unknown = "unknown";
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || unknown.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    /**
     * 踢出指定在线的用户
     *
     * @param username 用户名
     * @return 踢出的sessionId
     */
    public static void kickoutByUsername(String username) {
        List<Object> userList = sessionRegistry.getAllPrincipals();
        for (int i = 0; i < userList.size(); i++) {
            //如果该账号已登录，踢出之前登录账号
            SaltUser tempUser = (SaltUser) userList.get(i);
            if (tempUser.getUsername().equals(username)) {
                List<SessionInformation> sessionInformations = sessionRegistry.getAllSessions(tempUser, false);
                if (sessionInformations != null) {
                    for (int j = 0; j < sessionInformations.size(); j++) {
                        SessionInformation s = sessionInformations.get(j);
                        s.expireNow();
                        LoggerUtils.debug(OtherUtils.class, "kictout tempUser = %s", tempUser.getUsername());
                    }
                }
            }
        }
    }

    /**
     * 获取并返回session对应的用户
     * @param session   session
     * @return  获取不到返回null
     */
    public static SaltUser getUserBySession(HttpSession session) {
        SaltUser user = null;
        SessionInformation sessionInformations =
                sessionRegistry.getSessionInformation(session.getId());
        if (sessionInformations != null){
            user = (SaltUser) sessionInformations.getPrincipal();
        }
        return user;
    }

    /**
     * 判断用户是否在线
     *
     * @param username 用户名
     * @return 在线则返回用户实例，反之返回null
     */
    public static SaltUser isOnlineByUsername(String username) {
        SeUser user = returnUserByUsername(username);
        final SaltUser[] saltUser = {null};
        if (user != null) {
            List<Object> userList = sessionRegistry.getAllPrincipals();
            userList.forEach(u -> {
                SaltUser saltUser1 = (SaltUser) u;
                if (saltUser1.getUsername().equals(username)) {
                    saltUser[0] = saltUser1;
                    return;
                }
            });
        }
        return saltUser[0];
    }

    /**
     * 判断是否Ajax请求
     *
     * @param request 判断的请求
     * @return 是Ajax请求返回true，反之返回false
     */
    public static Boolean isAjaxRequest(HttpServletRequest request) {
        String ajaxHeader = "X-Requested-With";
        String ajaxRequest = "XMLHttpRequest";
        String header = request.getHeader(ajaxHeader);

        if (header != null && header.equalsIgnoreCase(ajaxRequest)) {
            return true;
        }
        return false;
    }

    /**
     * 返回登录用户对象
     *
     * @param username 用户名或邮箱
     * @return 返回User对象，查找不到返回null
     */
    public static SeUser returnUserByUsername(String username) {
        //判断email登录还是username登录，默认返回登录用户username名
        List<SeUser> list1 = seUserService.findByEmail(username);

        if (list1 == null) {
            list1 = seUserService.findByUsername(username);
        }

        return list1 == null ? null : list1.get(0);
    }

    /**
     * 查找SeRole数组中是否存在与用户的关联角色，执行添加操作
     *
     * @param roles 存在角色名的数组
     * @param user  查找的用户
     * @return 存在则返回SeUserRole数组，反之返回null
     */
    public static List<SeUserRole> selectAllUserRoleByAdd(List<String> roles, SeUser user) {
        //获取所有角色
        List<SeRole> roleList = seRoleService.findAllRole();
        List<SeUserRole> userRoleList = new ArrayList<SeUserRole>();

        for (String s : roles) {
            for (SeRole role : roleList) {
                if (s.equalsIgnoreCase(role.getDescription())) {
                    Integer userId = user.getId();
                    Integer roleId = role.getId();
                    List<SeUserRole> userRoles =
                            seUserRoleService.selectByUserIdRoleId(userId, roleId);
                    if (userRoles != null) {
                        break;
                    }
                    SeUserRole userRole = new SeUserRole(userId, roleId);
                    userRoleList.add(userRole);
                }
            }
        }

        return userRoleList.isEmpty() ? null : userRoleList;
    }

    /**
     * 查找SeRole数组中是否存在与用户的关联角色，执行删除操作
     *
     * @param roles 存在角色名的数组
     * @param user  查找的用户
     * @return 存在则返回SeUserRole数组，反之返回null
     */
    public static List<SeUserRole> selectAllUserRoleByDel(List<String> roles, SeUser user) {
        //获取所有角色
        List<SeRole> roleList = seRoleService.findAllRole();
        List<SeUserRole> userRoleList = new ArrayList<SeUserRole>();

        for (String s : roles) {
            for (SeRole role : roleList) {
                if (s.equalsIgnoreCase(role.getDescription())) {
                    Integer userId = user.getId();
                    Integer roleId = role.getId();
                    List<SeUserRole> userRoles =
                            seUserRoleService.selectByUserIdRoleId(userId, roleId);
                    if (userRoles == null) {
                        break;
                    }
                    SeUserRole userRole = new SeUserRole(userId, roleId);
                    userRoleList.add(userRole);
                }
            }
        }

        return userRoleList.isEmpty() ? null : userRoleList;
    }
}
