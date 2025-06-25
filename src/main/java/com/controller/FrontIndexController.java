package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;
import com.news.entity.HotNews;
import com.news.entity.PromotionNews;
import com.news.entity.News;
import com.news.service.HotNewsService;
import com.news.service.PromotionNewsService;
import com.news.service.NewsService;

@Controller
@RequestMapping("/")
public class FrontIndexController {
    @Autowired
    private HotNewsService hotNewsService;
    @Autowired
    private PromotionNewsService promotionNewsService;
    @Autowired
    private NewsService newsService;

    @GetMapping("/home")
    public String showHomepage() {
        // Logic to display the homepage
        return "index"; // This would typically return a view name
    }
}
