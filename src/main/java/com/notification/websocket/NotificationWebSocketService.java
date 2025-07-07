package com.notification.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationWebSocketService {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // 推送未讀數量給特定使用者(會員ID當作Principal name)
    public void sendUnreadCountToMember(Integer memberId, long unreadCount) {
        messagingTemplate.convertAndSendToUser(memberId.toString(), "/queue/notifications", unreadCount);
    }
}
