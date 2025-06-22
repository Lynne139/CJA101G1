package com.notification.service;

package com.notification.service;

import com.notification.entity.Member;
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

    // 取得某會員所有通知（不分頁）
    public List<Notification> getNotificationsByMember(Member member) {
        return notificationRepository.findByMember(member);
    }
}
