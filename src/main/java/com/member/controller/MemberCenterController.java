package com.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.member.model.MemberService;
import com.member.model.MemberVO;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberCenterController {

    @Autowired
    private MemberService memberSvc;

    // 會員中心頁面
    @GetMapping("/center")
    public String showMemberCenter(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/home";
        }
        model.addAttribute("loggedInMember", member);
        return "front-end/member/memberCenter";
    }

    // 顯示修改會員資料表單
    @GetMapping("/edit")
    public String editMember(Model model, HttpSession session) {
        MemberVO loginMember = (MemberVO) session.getAttribute("loggedInMember");
        if (loginMember == null) return "redirect:/home";
        model.addAttribute("memberVO", loginMember);
        return "front-end/member/editMember";
    }

    // 更新會員資料
    @PostMapping("/center/update")
    public String updateMember(@ModelAttribute MemberVO memberVO, HttpSession session) {
        MemberVO original = (MemberVO) session.getAttribute("loggedInMember");
        if (original == null) return "redirect:/home";

        memberVO.setMemberId(original.getMemberId());
        memberVO.setMemberEmail(original.getMemberEmail());
        memberVO.setMemberLevel(original.getMemberLevel());

        memberSvc.updateMember(memberVO);
        session.setAttribute("loggedInMember", memberVO);
        return "redirect:/member/center";
    }
}
