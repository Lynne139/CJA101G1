package com.news.controller;

import com.news.entity.HotNews;
import com.news.service.HotNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/admin/news")
public class HotNewsController {
    @Autowired
    private HotNewsService service;

    @GetMapping
    public String list(Model model) {
        model.addAttribute("hotNewsList", service.findAll());
        model.addAttribute("hotNews", new HotNews());
        return "admin/fragments/news/news1";
    }

    @PostMapping("/add")
    public String add(@ModelAttribute HotNews hotNews, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        if (photo != null && !photo.isEmpty()) {
            hotNews.setNewsPhoto(photo.getBytes());
        }
        hotNews.setCreatedDate(LocalDate.now());
        service.save(hotNews);
        return "redirect:/admin/news1";
    }

    @PostMapping("/edit")
    public String edit(@ModelAttribute HotNews hotNews, @RequestParam(value = "photo", required = false) MultipartFile photo) throws IOException {
        Optional<HotNews> optional = service.findById(hotNews.getHotNewsNo());
        if (optional.isPresent()) {
            HotNews original = optional.get();
            original.setTitle(hotNews.getTitle());
            original.setContent(hotNews.getContent());
            original.setIsDisplay(hotNews.getIsDisplay());
            if (photo != null && !photo.isEmpty()) {
                original.setNewsPhoto(photo.getBytes());
            }
            // createdDate 保留原本值
            service.save(original);
        }
        return "redirect:/admin/news1";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        service.deleteById(id);
        return "redirect:/admin/news1";
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public byte[] image(@PathVariable Integer id) {
        Optional<HotNews> news = service.findById(id);
        return news.map(HotNews::getNewsPhoto).orElse(null);
    }
}
