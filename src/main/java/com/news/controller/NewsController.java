package com.news.controller;

import com.news.entity.News;
import com.news.service.NewsService;
import com.news.service.HotNewsService;
import com.news.service.PromotionNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/news3")
public class NewsController {
    @Autowired
    private NewsService service;
    @Autowired
    private HotNewsService hotNewsService;
    @Autowired
    private PromotionNewsService promotionNewsService;

    @PostMapping("/add")
    public String add(@ModelAttribute News news, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            news.setNewsPhoto(photo.getBytes());
        }
        news.setPublishedDate(LocalDate.now());
        service.save(news);
        return "redirect:/admin/news3";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute News news, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        Optional<News> optional = service.findById(news.getNewsNo());
        if (optional.isPresent()) {
            News original = optional.get();
            original.setTitle(news.getTitle());
            original.setContent(news.getContent());
            original.setIsDisplay(news.getIsDisplay());
            if (photo != null && !photo.isEmpty()) {
                original.setNewsPhoto(photo.getBytes());
            }
            // publishedDate 保留原本值
            service.save(original);
        }
        return "redirect:/admin/news3";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/admin/news3";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] image(@PathVariable Integer id) {
        Optional<News> news = service.findById(id);
        return news.map(News::getNewsPhoto).orElse(null);
    }

    @GetMapping("/admin/news3")
    public String showNews3(Model model) {
        model.addAttribute("newsList", service.findAll());
        return "admin/fragments/news/news3";
    }
} 