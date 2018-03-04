package com.evy.jing.security;

import com.evy.jing.model.SeUser;
import com.evy.jing.service.SeUserService;
import com.evy.jing.util.LoggerUtils;
import com.evy.jing.util.OtherUtils;
import com.evy.jing.websocket.MessagePushUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * 记录用户最后登录时间与IP
 * SimpleUrlAuthenticationSuccessHandler    认证成功基于角色登陆
 */
@Component
public class RecordLoginTimeFilter extends SimpleUrlAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Resource
    SeUserService seUserService;
    @Resource
    MessagePushUtil messagePushUtil;
    private final String success_url = "/main";
    private final String access_denied_url = "/Access_Denied";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
                                        HttpServletResponse httpServletResponse,
                                        Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        WebAuthenticationDetails webAuthenticationDetails = (WebAuthenticationDetails) authentication.getDetails();

        String username = userDetails.getUsername();

//        List<SeUser> list = seUserService.findByUsername(username);
//        if (list == null){
//            list = seUserService.findByEmail(username);
//        }
//        OtherUtils.returnUserByUsername(username);
//        Assert.notNull(list, "记录用户最后登录时间出现错误。");

        SeUser seUser = OtherUtils.returnUserByUsername(username);
        Assert.notNull(seUser, "记录用户最后登录时间出现错误。");
        seUser.setLastTime(new Date());

        //更新用户最后登录时间
        seUserService.update(seUser);
        //获取用户IP
        String ip = webAuthenticationDetails.getRemoteAddress();
        LoggerUtils.debug(getClass(), "当前登录用户IP: %s", ip);

        //通知用户在线
        messagePushUtil.noticeIsOnlinebyUsername(username, true);

        /*START 基于角色登陆*/
        if (!httpServletResponse.isCommitted()) {
            String url = "";
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            List<String> roles = new ArrayList<String>();
            //获取角色名
            for (GrantedAuthority grantedAuthority : authorities) {
                roles.add(grantedAuthority.getAuthority());
            }
            if (isRoot(roles)) {
                url = success_url;
            } else if (isUser(roles)) {
                url = success_url;
            } else {
                url = access_denied_url;
            }
            httpServletResponse.sendRedirect(url);
        }
        /*END 基于角色登陆*/

//        SavedRequestAwareAuthenticationSuccessHandler successHandler =
//                new SavedRequestAwareAuthenticationSuccessHandler();
//        successHandler.onAuthenticationSuccess(httpServletRequest, httpServletResponse, authentication);
    }

    private boolean isRoot(List<String> role){
        if (role.contains("ROLE_ROOT")){
            return true;
        }
        return false;
    }

    private boolean isUser(List<String> role){
        if (role.contains("ROLE_USER")){
            return true;
        }
        return false;
    }
}
