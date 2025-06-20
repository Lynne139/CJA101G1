package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class FrontIndexController {
    @GetMapping("/home")
    public String showHomepage() {
        // Logic to display the homepage
        return "index"; // This would typically return a view name
    }
}
