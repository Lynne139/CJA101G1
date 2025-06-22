package com.notification.controller;

import com.member.model.MemberVO;
import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 這段是假設已經有 Member 是從登入資訊中取得，以下提供 REST API 範例：
// 若是用伺服器渲染，要重寫!!!!!


@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // 假設你前端送的 JSON 有包含 memberId
    @PostMapping
    public Notification createNotification(@RequestBody Notification notification) {
        return notificationService.createNotification(notification);
    }

    // 查詢特定會員的通知
    @GetMapping("/member/{memberId}")
    public List<Notification> getNotificationsByMember(@PathVariable("memberId") Integer memberId) {
        // TODO: 改成從資料庫查出 Member 物件，你可能有 MemberService
        MemberVO member = new MemberVO();
        member.setMemberId(memberId);
        return notificationService.getNotificationsByMember(member);
    }
}
