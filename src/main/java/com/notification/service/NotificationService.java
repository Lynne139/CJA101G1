package com.notification.service;

import com.notification.entity.Notification;
import com.notification.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
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
        return notificationRepository.save(notification);
    }

    // 取得某會員所有通知
    public List<Notification> getNotificationsByMemberId(Integer memberId) {
        return notificationRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
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
