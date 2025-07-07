package com.cs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/cs")
// 後台客服表單頁面控制器
public class AdminCSPageController {
    
    // 管理員查看並回覆單一留言頁面
    @GetMapping("/reply/{messageId}")
    public String csReplyPage(@PathVariable String messageId, HttpServletRequest request, Model model) {
        model.addAttribute("messageId", messageId); // 把 messageId 傳給前端
        model.addAttribute("mainFragment", "admin/fragments/cs/reply-cs");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }

}
