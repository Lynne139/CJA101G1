package com.coupon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/coupon")
// 前台折價券頁面控制器
public class FrontCouponPageController {

	// 會員查詢持有的折價券頁面
    @GetMapping("/select")
    public String memberCouponSelectPage() {
        return "front-end/coupon/member-select-coupon";
    }
    
	// 會員領取折價券頁面
    @GetMapping("/claim")
    public String memberCouponClaimPage() {
        return "front-end/coupon/claim-coupon";
    }
    
}
