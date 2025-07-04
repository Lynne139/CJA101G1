package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class TrafficController {

    @GetMapping("/traffic")
    public String showTrafficInfo() {
        return "front-end/traffic/traffic-info";
    }
} 