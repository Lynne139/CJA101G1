package com.coupon.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/coupon")
// 後台折價券頁面控制器
public class AdminCouponPageController {

//	// 管理員查詢折價券頁面
//	@GetMapping("/select")
//    public String couponSelectPage(HttpServletRequest request, Model model) {
//        model.addAttribute("mainFragment", "admin/fragments/coupon/admin-select-coupon");
//        model.addAttribute("currentURI", request.getRequestURI());
//        return "admin/index_admin";
//    }
	
	// 管理員新增折價券頁面
    @GetMapping("/add")
    public String couponAddPage(HttpServletRequest request, Model model) {
        model.addAttribute("mainFragment", "admin/fragments/coupon/add-coupon");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }
    
    // 管理員修改折價券頁面
    @GetMapping("/update/{couponCode}")
    public String couponUpdatePage(@PathVariable String couponCode, HttpServletRequest request, Model model) {
        model.addAttribute("couponCode", couponCode); // 把 couponcode 傳給前端 JS 用
        model.addAttribute("mainFragment", "admin/fragments/coupon/update-coupon");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }

}
