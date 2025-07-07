package com.controller;

import java.time.LocalDateTime;
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
import com.notification.service.NotificationService;

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
    
    @Autowired
    private NotificationService notificationService;

    @GetMapping("/home")
    public String showHomepage(Model model, HttpSession session, 
                              @RequestParam(required = false) String loginRequired) {
        model.addAttribute("memberVO", new MemberVO());

        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        model.addAttribute("loggedInMember", loggedInMember);

        // 處理登入要求參數
        if ("true".equals(loginRequired)) {
            model.addAttribute("forceLogin", true);
            model.addAttribute("loginMessage", "請先登入會員才能使用此功能");
        }

        // 新增：查詢三種最新消息
        hotNewsService.findLatestDisplay().ifPresent(hotNews -> model.addAttribute("latestHotNews", hotNews));
        promotionNewsService.findLatestDisplay().ifPresent(promotionNews -> model.addAttribute("latestPromotionNews", promotionNews));
        newsService.findLatestDisplay().ifPresent(news -> model.addAttribute("latestMediaNews", news));

        return "index";
    }
    
    
    @GetMapping("/")
    public String rootRedirect(Model model, HttpSession session,
                              @RequestParam(required = false) String loginRequired) {
        model.addAttribute("memberVO", new MemberVO());
        MemberVO loggedInMember = (MemberVO) session.getAttribute("loggedInMember");
        model.addAttribute("loggedInMember", loggedInMember);

        // 處理登入要求參數
        if ("true".equals(loginRequired)) {
            model.addAttribute("forceLogin", true);
            model.addAttribute("loginMessage", "請先登入會員才能使用此功能");
        }

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
            // 登入成功後新增通知
            String content = "親愛的 " + dbMember.getMemberName() + "，您已於 " + LocalDateTime.now() + " 成功登入。";
            notificationService.createNotification(dbMember.getMemberId(), "登入成功通知", content);
        }

        return result;
    }
    
    @GetMapping("/api/member/refresh")
    @ResponseBody
    public MemberVO refreshMember(HttpSession session) {
        MemberVO sessionMember = (MemberVO) session.getAttribute("loggedInMember");

        if (sessionMember == null) {
            return null; // 或回傳錯誤訊息物件
        }

        // 從資料庫重新查最新資料
        MemberVO updated = memberSvc.getOneMember(sessionMember.getMemberId());

        // 放回 session，讓 Thymeleaf 也能看到新資料
        session.setAttribute("loggedInMember", updated);

        return updated;
    }
    
    @GetMapping("/member/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/home";
    }
    
    
}
