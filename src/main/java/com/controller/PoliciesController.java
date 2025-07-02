package com.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/policies")
public class PoliciesController {

    @GetMapping("/privacy")
    public String showPrivacyPolicy() {
        return "front-end/policies/privacy";
    }

    @GetMapping("/terms")
    public String showTermsOfService() {
        return "front-end/policies/terms";
    }

    @GetMapping("/cookies")
    public String showCookiesPolicy() {
        return "front-end/policies/cookies";
    }
} 