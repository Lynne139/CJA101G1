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

    // 取得某會員所有通知
    public List<Notification> getNotificationsByMemberId(Integer memberId) {
        return notificationRepository.findByMemberId(memberId);
    }
    
    // 更新通知為已讀狀態
    public boolean markAsRead(Integer notificationId, Integer memberId) {
        Notification notification = notificationRepository.findByNotificationIdAndMemberId(notificationId, memberId);
        if (notification != null && !notification.getIsRead()) {
            notification.setIsRead(true);
            notificationRepository.save(notification);
            return true; // 表示更新成功
        }
        return false; // 找不到或已是已讀狀態
    }
}
