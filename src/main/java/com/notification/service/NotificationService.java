package com.notification.service;

import com.notification.entity.Notification;
import com.notification.repository.NotificationRepository;
import com.notification.websocket.NotificationWebSocketService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private NotificationWebSocketService notificationWebSocketService;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
    		NotificationWebSocketService notificationWebSocketService) {
        this.notificationRepository = notificationRepository;
        this.notificationWebSocketService = notificationWebSocketService;
    }

    // 新增通知
    public Notification createNotification(Notification notification) {
        return notificationRepository.save(notification);
    }
    
    // 更簡單使用的新增通知
    public Notification createNotification(Integer memberId, String title, String content) {
        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setIsRead(false); // 預設未讀
        Notification saved = notificationRepository.save(notification);
        // 計算最新未讀數量
        long unreadCount = notificationRepository.countUnreadByMemberId(memberId);
        // 推播給該會員
        notificationWebSocketService.sendUnreadCountToMember(memberId, unreadCount);
        
        return saved;
    }

    // 取得某會員所有通知
    public List<Notification> getNotificationsByMemberId(Integer memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }
    
    // 根據會員編號查詢未讀通知數量
    public long getUnreadNotificationCount(Integer memberId) {
        return notificationRepository.countUnreadByMemberId(memberId);
    }
    
    // 更新通知為已讀狀態
    public boolean markAsRead(Integer notificationId, Integer memberId) {
        Notification notification = notificationRepository.findByNotificationIdAndMemberId(notificationId, memberId);
        if (notification != null && !notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return true; // 成功更新為已讀
        }
        return false; // 通知不存在或已是已讀
    }

}
