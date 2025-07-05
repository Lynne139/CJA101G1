package com.notification.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/members")
public class NotificationPageController {

    @GetMapping("/notifications")
    public String notificationPage() {
        return "front-end/notification/notifications";
    }
}
