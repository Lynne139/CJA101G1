package com.member.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/member")
public class MemberLoginController {

    @Autowired
    private MemberService memberSvc;

//    // 顯示登入頁面    
//    @GetMapping("/memberLogin")
//    public String showLoginPage(ModelMap model) {
//        model.addAttribute("memberVO", new MemberVO()); // 綁定表單
//        model.addAttribute("mainFragment", "front-end/member/memberLogin :: content");
//        return "index";
//    }
//    
//    // 登入驗證
//    @PostMapping("/memberLogin")
//    public String login(@RequestParam("memberEmail") String email,
//                        @RequestParam("memberPassword") String password,
//                        HttpSession session,
//                        ModelMap model) {
//
//        MemberVO member = memberSvc.findByEmail(email);
//
//        if (member == null || !member.getMemberPassword().equals(password)) {
//            model.addAttribute("error", "帳號或密碼錯誤");
//            model.addAttribute("mainFragment", "front-end/member/memberLogin :: content");
//          
//            return "index";
//        }
//
//        session.setAttribute("loginMember", member);
//        return "redirect:/member/memberCenter";
//    }
//
//    // 登出功能
//    @GetMapping("/memberLogout")
//    public String logout(HttpSession session) {
//        session.invalidate();
//        return "redirect:/";
//    }
//    
//    // memberCenter
//    @GetMapping("/memberCenter")
//    public String memberCenter(HttpSession session, ModelMap model) {
//        if (session.getAttribute("loginMember") == null) {
//            return "redirect:/member/memberLogin";
//        }
//        model.addAttribute("mainFragment", "front-end/member/memberCenter :: memberCenterFragment");
//        return "index";
//    }
    
    @GetMapping("/")
    public String showIndex(Model model) {
        model.addAttribute("memberVO", new MemberVO()); 
        return "index";
    }

}
