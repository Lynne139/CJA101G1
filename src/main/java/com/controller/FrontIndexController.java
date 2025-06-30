package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.news.service.HotNewsService;
import com.news.service.NewsService;
import com.news.service.PromotionNewsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/")
public class FrontIndexController {
    @Autowired
    private HotNewsService hotNewsService;
    @Autowired
    private PromotionNewsService promotionNewsService;
    @Autowired
    private NewsService newsService;
    
    @Autowired
    private MemberService memberSvc;

    @GetMapping("/home")
    public String showHomepage(Model model, HttpSession session) {
        model.addAttribute("memberVO", new MemberVO());

        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        model.addAttribute("loggedInMember", loggedInMember); 

        return "index";
    }
    
    
    @GetMapping("/")
    public String rootRedirect(Model model, HttpSession session) {
        model.addAttribute("memberVO", new MemberVO());
        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        model.addAttribute("loggedInMember", loggedInMember);
        return "index";
    }

    
    @PostMapping("/member/memberLogin")
    public String login(@ModelAttribute("memberVO") MemberVO memberVO,
                        Model model,
                        HttpSession session) {

        MemberVO dbMember = memberSvc.findByEmail(memberVO.getMemberEmail());

        if (dbMember == null || !dbMember.getMemberPassword().equals(memberVO.getMemberPassword())) {
            model.addAttribute("error", "帳號或密碼錯誤");
            model.addAttribute("memberVO", new MemberVO());
            return "index";
        }
        
        session.setAttribute("loggedInMember", dbMember);
        return "redirect:/home";
    }
    
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
