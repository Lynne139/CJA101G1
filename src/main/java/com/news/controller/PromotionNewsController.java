package com.news.controller;

import com.news.entity.PromotionNews;
import com.news.service.PromotionNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/promotionNews")
public class PromotionNewsController {
    @Autowired
    private PromotionNewsService service;

    @PostMapping("/add")
    public String add(@ModelAttribute PromotionNews promotionNews, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            promotionNews.setPromoPhoto(photo.getBytes());
        }
        service.save(promotionNews);
        return "redirect:/admin/news2";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute PromotionNews promotionNews, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            promotionNews.setPromoPhoto(photo.getBytes());
        }
        service.save(promotionNews);
        return "redirect:/admin/news2";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/admin/news2";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] image(@PathVariable Integer id) {
        Optional<PromotionNews> news = service.findById(id);
        return news.map(PromotionNews::getPromoPhoto).orElse(null);
    }
} 