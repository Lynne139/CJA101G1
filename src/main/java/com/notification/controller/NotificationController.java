package com.notification.controller;

import com.notification.entity.Notification;
import com.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // 查詢指定會員通知（預設每頁15筆）
    @GetMapping("/member/{memberId}")
    public String getNotificationsByMember(
            @PathVariable("memberId") Integer memberId,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        List<Notification> allNotifications = notificationService.getNotificationsByMemberId(memberId);

        int pageSize = 15;
        int total = allNotifications.size();
        int start = page * pageSize;
        int end = Math.min(start + pageSize, total);
        List<Notification> currentPageList = allNotifications.subList(start, end);

        Page<Notification> notificationPage = new PageImpl<>(currentPageList, PageRequest.of(page, pageSize), total);

        model.addAttribute("notificationPage", notificationPage);
        model.addAttribute("memberId", memberId);

        return "notifications";
    }
    
    @PostMapping("/{notificationId}/read")
    public String markNotificationAsRead(
            @PathVariable("notificationId") Integer notificationId,
            @RequestParam("memberId") Integer memberId,
            RedirectAttributes redirectAttributes) {

        boolean updated = notificationService.markAsRead(notificationId, memberId);

        if (updated) {
            redirectAttributes.addFlashAttribute("successMessage", "通知已設為已讀");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "通知不存在或不屬於該會員");
        }

        // 導回會員的通知頁面
        return "redirect:/notifications/member/" + memberId;
    }
}

