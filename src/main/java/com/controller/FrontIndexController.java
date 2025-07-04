package com.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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

        // 新增：查詢三種最新消息
        hotNewsService.findLatestDisplay().ifPresent(hotNews -> model.addAttribute("latestHotNews", hotNews));
        promotionNewsService.findLatestDisplay().ifPresent(promotionNews -> model.addAttribute("latestPromotionNews", promotionNews));
        newsService.findLatestDisplay().ifPresent(news -> model.addAttribute("latestMediaNews", news));

        return "index";
    }
    
    
    @GetMapping("/")
    public String rootRedirect(Model model, HttpSession session) {
        model.addAttribute("memberVO", new MemberVO());
        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        model.addAttribute("loggedInMember", loggedInMember);

        // 新增：查詢三種最新消息
        hotNewsService.findLatestDisplay().ifPresent(hotNews -> model.addAttribute("latestHotNews", hotNews));
        promotionNewsService.findLatestDisplay().ifPresent(promotionNews -> model.addAttribute("latestPromotionNews", promotionNews));
        newsService.findLatestDisplay().ifPresent(news -> model.addAttribute("latestMediaNews", news));

        return "index";
    }

    
    
    
    // Ajax 版登入
    @PostMapping("/member/ajaxLogin")
    @ResponseBody
    public Map<String, Object> ajaxLogin(@RequestParam String memberEmail,
                                         @RequestParam String memberPassword,
                                         HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        MemberVO dbMember = memberSvc.findByEmail(memberEmail);

        if (dbMember == null || !dbMember.getMemberPassword().equals(memberPassword)) {
            result.put("success", false);
            result.put("message", "帳號或密碼錯誤");
        } else if (dbMember.getMemberStatus() == 2) {
            result.put("success", false);
            result.put("message", "此帳號已被停權，請聯絡客服");
        } else {
            session.setAttribute("loggedInMember", dbMember);
            result.put("success", true);
        }

        return result;
    }
    
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
}
