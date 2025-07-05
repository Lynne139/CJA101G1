package com.notification.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 設定簡單的訊息代理，支援 /topic 和 /queue
        config.enableSimpleBroker("/topic", "/queue");
        // 設定前端發送訊息時的前綴
        config.setApplicationDestinationPrefixes("/app");
        // 使用者專屬訊息的前綴
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 註冊WebSocket端點並允許跨域（允許任何來源）
        registry.addEndpoint("/ws-notifications")
            .addInterceptors(new HttpHandshakeInterceptor())
            .setHandshakeHandler(new CustomHandshakeHandler())
            .setAllowedOriginPatterns("*")
            .withSockJS();
    }
}