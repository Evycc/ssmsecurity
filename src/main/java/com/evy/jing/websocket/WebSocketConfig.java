package com.evy.jing.websocket;

import com.evy.jing.Interceptor.HandShakeInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import javax.annotation.Resource;

/**
 * websocket配置
 * @author : Evy
 * @date : Created 2018/1/27 22:40
 */
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer{
    @Resource
    private MessageHandler messageHandler;
    @Resource
    private HandShakeInterceptor interceptor;
    private final String WSPATH = "/websocket";
    private final String SOCKJSPATH= "/sockjs/websocket";

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry.addHandler(messageHandler, WSPATH)
                .addInterceptors(interceptor);
        webSocketHandlerRegistry.addHandler(messageHandler, SOCKJSPATH)
                .addInterceptors(interceptor).withSockJS();
    }
}
