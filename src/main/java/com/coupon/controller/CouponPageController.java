package com.coupon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class CouponPageController {

	// 顯示查詢折價券頁面
	@GetMapping("/coupon/select_page")
    public String couponSelectPage(HttpServletRequest request, Model model) {
        model.addAttribute("mainFragment", "admin/fragments/coupon/select_page");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }
	
	// 顯示新增折價券頁面
    @GetMapping("/coupon/add")
    public String couponAddPage(HttpServletRequest request, Model model) {
        model.addAttribute("mainFragment", "admin/fragments/coupon/addCoupon");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }
    
    // 顯示修改折價券頁面
    @GetMapping("/coupon/update/{couponCode}")
    public String couponUpdatePage(@PathVariable String couponCode, HttpServletRequest request, Model model) {
        model.addAttribute("couponCode", couponCode); // 把 couponcode 傳給前端 JS 用
        model.addAttribute("mainFragment", "admin/fragments/coupon/updateCoupon");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }

}
