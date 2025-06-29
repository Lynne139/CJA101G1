package com.notification.controller;

import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 查詢會員通知
    @GetMapping("/member/{memberId}")
    public List<Notification> getAllNotifications(@PathVariable Integer memberId) {
        return notificationService.getNotificationsByMemberId(memberId);
    }

    // 設為已讀
    @PostMapping("/{memberId}/{notificationId}/read")
    public String markNotificationAsRead(
            @PathVariable Integer memberId,
            @PathVariable Integer notificationId) {

        boolean success = notificationService.markAsRead(notificationId, memberId);
        return success ? "success" : "fail";
    }

}
