package com.evy.jing.Interceptor;

import com.evy.jing.security.SaltUser;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * websocket握手拦截器
 *
 * @author : Evy
 * @date : Created 2018/1/28 0:05
 */
@Component
public class HandShakeInterceptor extends HttpSessionHandshakeInterceptor {
    /**
     * 握手请求之前调用
     *
     * @param request
     * @param response
     * @param wsHandler  websocket目标处理程序
     * @param attributes 握手属性
     * @return 继续握手返回true，否则返回false
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        Object object = SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        if (object instanceof SaltUser){
            SaltUser user = (SaltUser) object;
            attributes.put("username", user);
        }
        return super.beforeHandshake(request, response, wsHandler, attributes);
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, @Nullable Exception ex) {
        super.afterHandshake(request, response, wsHandler, ex);
    }
}
