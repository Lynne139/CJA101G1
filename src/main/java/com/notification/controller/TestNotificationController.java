package com.notification.controller;

import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test/notifications")
public class TestNotificationController {

    @Autowired
    private NotificationService notificationService;

    /**
     * 測試用：建立一則測試通知（模擬後端自動通知）
     * 範例: /test/notifications/create?memberId=1&content=這是一則測試通知
     */

    @GetMapping("/create")
    public String createTestNotification(
            @RequestParam("memberId") Integer memberId,
            @RequestParam(value = "content", defaultValue = "這是一則測試通知") String content,
            @RequestParam(value = "title", defaultValue = "系統通知") String title) {

        Notification notification = new Notification();
        notification.setMemberId(memberId);
        notification.setTitle(title);        // 一定要設定 title
        notification.setContent(content);
        notification.setIsRead(false);

        Notification saved = notificationService.createNotification(notification);

        return "測試通知已建立，通知ID: " + saved.getNotificationId();
    }


}
