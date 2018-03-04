package com.evy.jing.security;

import com.evy.jing.util.LoggerUtils;
import com.evy.jing.websocket.MessagePushUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 注销后用户操作控制
 *
 * @author : Evy
 * @date : Created 2017/12/4 17:34
 */
public class LogoutHandle implements LogoutSuccessHandler {
    @Resource
    SessionRegistry sessionRegistry;
    @Resource
    MessagePushUtil messagePushUtil;
    private final String logout = "/";

    @Override
    public void onLogoutSuccess(HttpServletRequest httpServletRequest,
                                HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        if (authentication != null){
            //注销的sessionId
            String logoutSessionId = sessionRegistry.getAllSessions(authentication.getPrincipal(),
                    false).get(0).getSessionId();
            //将注销用户的sessionId删除
            sessionRegistry.removeSessionInformation(logoutSessionId);

            //通知用户离线
            Object object = authentication.getPrincipal();
            if (object != null){
                SaltUser user = (SaltUser) object;
                messagePushUtil.noticeIsOnlinebyUsername(user.getUsername(), false);
            }
        }

        //注销后跳转url
        httpServletResponse.sendRedirect(logout);
    }

}
