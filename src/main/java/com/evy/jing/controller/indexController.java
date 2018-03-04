package com.evy.jing.controller;

import com.evy.jing.EnumMsg.EnumLoginError;
import com.evy.jing.redis.JedisCache;
import com.evy.jing.util.LoggerUtils;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 页面跳转控制器
 */
@Controller
public class indexController {
    @Resource
    private JedisCache jedisCache;
    /**
     * 登录账号或密码错误
     */
    private static final String EXCEPTIOIN_BAD = "BadCredentialsException";
    /**
     * 账号锁定错误
     */
    private static final String EXCEPTION_LOCKED = "LockedException";
    /**
     * 账号未启动错误
     */
    private static final String EXCEPTION_DISABLED = "DisabledException";
    /**
     * 密码凭证过期错误
     */
    private static final String EXCEPTION_CREDENTIALS = "CredentialsExpiredException";
    private static final String EXCEPTION_USERNAMENOTFOUND = "UsernameNotFoundException";

    /**
     * 主页
     * @return
     */
    @RequestMapping("/")
    public String indexPage(){
        return "/views/welcome.html";
    }

    @RequestMapping("/user")
    public String adminPage(){
        //test
        return "/views/user.html";
    }

    @RequestMapping("/root")
    public String rootPage(){
        //test
        return "/views/testROOT.html";
    }

    @RequestMapping(value = {"/404","/Access_Denied"})
    public String errPage(HttpServletRequest request){
        return "/views/404.html";
    }

    /**
     * 更改密码页
     * @return
     */
    @RequestMapping(value = "/changePasswordPage", method = RequestMethod.GET)
    public String changePasswordPage(){
        return "/views/changePasswordPage.html";
    }

    /**
     * 用户主页
     * @return
     */
    @RequestMapping(value = "/main")
    public String userMainPage(){
        return "/views/main.html";
    }

    /**
     * 登录页
     * @param session
     * @param kickout   是否带有被踢出标志
     * @param changePassword    是否带有密码被更改标志
     * @return
     */
    @RequestMapping(value = "/login")
    public String loginPage(HttpSession session,
                            @RequestParam(value = "kickout", required = false)String kickout,
                            @RequestParam(value = "changePassword", required = false)String changePassword){
        Object hasErrorMessage = session.getAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
        if (hasErrorMessage != null) {
            //异常名称
            String messageName = hasErrorMessage.getClass().getSimpleName();

            if (EXCEPTIOIN_BAD.equals(messageName) ||
                    EXCEPTION_USERNAMENOTFOUND.equals(messageName)){
                jedisCache.put(EnumLoginError.ERROR_LOGIN_PASSWORD, "账号或密码错误");

                session.removeAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
                return "/views/login.html";
            }else if(EXCEPTION_LOCKED.equals(messageName)){
                jedisCache.put(EnumLoginError.ERROR_LOGIN_LOCKED, "账号已被锁定");

                session.removeAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
                return "/views/login.html";
            }else if (EXCEPTION_DISABLED.equals(messageName)){
                jedisCache.put(EnumLoginError.ERROR_LOGIN_DISABLE, "账号未启动");

                session.removeAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
                return "/views/login.html";
            }else if (EXCEPTION_CREDENTIALS.equals(messageName)){
                jedisCache.put(EnumLoginError.ERROR_LOGIN_CREDENTIAL, "密码凭证已过期");

                session.removeAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
                return "/views/login.html";
            }else {
                LoggerUtils.errorStr(getClass(),"未知错误:%s。", messageName);
                jedisCache.put(EnumLoginError.ERROR_OTHER, "未知错误");

                session.removeAttribute(EnumLoginError.SPRING_SECURITY_LAST_EXCEPTION.toString());
                return "/views/login.html";
            }
        }

        //密码更改判断
        if (jedisCache.get(EnumLoginError.CHANGE_PASSWORD) != null){
            return "/views/login.html";
        }

        //判断是否用户被踢出
        if (kickout != null) {
            jedisCache.put(EnumLoginError.ERROR_KICKOUT, "您的账号在其他地方登陆!您已下线。");
        }

        return "/views/login.html";
    }

    /**
     * 重置密码页
     * @return
     */
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET)
    public String resetPasswordPage(){
        return "/views/resetPasswordPage.html";
    }
}
