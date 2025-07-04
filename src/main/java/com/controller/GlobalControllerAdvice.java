package com.controller;

import com.member.model.MemberVO;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {

    /**
     * 統一處理登入狀態
     * 會在每個控制器方法執行前自動執行
     * 將登入會員資訊添加到所有頁面的model中
     * 攔截介面請看MemberFilter
     */
    @ModelAttribute("loggedInMember")
    public MemberVO addLoggedInMember(HttpSession session) {
        return (MemberVO) session.getAttribute("loggedInMember");
    }
} 