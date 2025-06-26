package com.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/members")
public class NotificationPageController {

    @GetMapping("/{memberId}/notifications")
    public String notificationPage(@PathVariable Integer memberId, Model model) {
        model.addAttribute("memberId", memberId); // 給前端 JavaScript 用來發 AJAX
        return "front-end/notification/notifications";
    }
}
