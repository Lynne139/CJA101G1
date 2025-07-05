package com.notification.controller;

import com.member.model.MemberVO;
import com.notification.entity.Notification;
import com.notification.service.NotificationService;

import jakarta.servlet.http.HttpSession;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 查詢會員通知
    @GetMapping("/member")
    public List<Notification> getAllNotifications(HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
        return notificationService.getNotificationsByMemberId(memberId);
    }
    
    // 查詢未讀通知數量
    @GetMapping("/unread-count")
    public long getUnreadCount(HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
        return notificationService.getUnreadNotificationCount(memberId);
    }
    
    // 設為已讀
    @PostMapping("/{notificationId}/read")
    public String markNotificationAsRead(
            @PathVariable Integer notificationId,
            HttpSession session) {
    	MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
    	Integer memberId = loggedInMember.getMemberId();
        boolean success = notificationService.markAsRead(notificationId, memberId);
        return success ? "success" : "fail";
    }

}
