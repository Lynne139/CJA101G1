package com.news.controller;

import com.news.service.NewsService;
import com.news.entity.HotNews;
import com.news.service.HotNewsService;
import com.news.service.PromotionNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/news")
public class PublicNewsController {
    @Autowired
    private NewsService service;
    @Autowired
    private HotNewsService hotNewsService;
    @Autowired
    private PromotionNewsService promotionNewsService;

    @GetMapping("/news")
    public String showMediaNews(Model model) {
        model.addAttribute("newsList", service.findAll());
        return "news/news";
    }

    @GetMapping("/notice")
    public String showNotice(Model model) {
        List<HotNews> hotNewsList = hotNewsService.findAll();
        // 強制觸發 newsPhoto，避免 LazyInitializationException 或 null
        for (HotNews news : hotNewsList) {
            if (news.getNewsPhoto() != null) {
                int len = news.getNewsPhoto().length;
            }
        }
        model.addAttribute("hotNewsList", hotNewsList);
        return "news/notice";
    }

    @GetMapping("/promotion")
    public String showPromotion(Model model) {
        model.addAttribute("promotionNewsList", promotionNewsService.findAll());
        return "news/promotion";
    }
} 