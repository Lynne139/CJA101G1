package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/front-end/about")
public class AboutController {

    @GetMapping("/brand-story")
    public String showBrandStory() {
        return "front-end/about/brand-story";
    }

    @GetMapping("/environment")
    public String showEnvironment() {
        return "front-end/about/environment";
    }
} 