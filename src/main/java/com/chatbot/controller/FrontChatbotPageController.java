package com.chatbot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
// 前台智慧客服機器人頁面控制器
public class FrontChatbotPageController {

    @GetMapping("/chatbot")
    public String chatbotPage() {
        return "front-end/chatbot/chatbot";
    }
    
}
