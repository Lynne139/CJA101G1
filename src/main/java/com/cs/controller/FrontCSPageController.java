package com.cs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
// 前台客服表單頁面控制器
public class FrontCSPageController {
	
    @GetMapping("/cs")
    public String customerServicePage(HttpSession session, Model model) {
        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        if (loggedInMember != null) {
            model.addAttribute("memberId", loggedInMember.getMemberId());
            model.addAttribute("memberName", loggedInMember.getMemberName());
            model.addAttribute("memberEmail", loggedInMember.getMemberEmail());
        }

        return "front-end/cs/cs-form";
    }
}
