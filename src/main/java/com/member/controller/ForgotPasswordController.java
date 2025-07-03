//package com.member.controller;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.member.model.MemberService;
//
//@Controller
//public class ForgotPasswordController {
//
//    @Autowired
//    private MemberService memberSvc;
//
//    // 顯示忘記密碼頁面
//    @GetMapping("/front-end/member/forgot-password")
//    public String showForgotPasswordForm() {
//        return "front-end/member/forgot-password";
//    }
//
//    // 接收忘記密碼表單提交
//    @PostMapping("/front-end/member/send-reset-link")
//    public String processForgotPassword(
//            @RequestParam("email") String email,
//            Model model) {
//
//        boolean sent = memberSvc.sendResetPasswordEmail(email);
//
//        if (sent) {
//            model.addAttribute("message", "重設連結已寄至您的信箱,請於 5 分鐘內使用");
//        } else {
//            model.addAttribute("error", "Email 未註冊或發送失敗。");
//        }
//
//        return "front-end/member/forgot-password";
//    }
//}
