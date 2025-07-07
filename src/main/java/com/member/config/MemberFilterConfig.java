package com.member.config;

import com.member.filter.FrontEndLoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MemberFilterConfig {

    /**
     * 註冊前台登入過濾器
     */
    @Bean
    public FilterRegistrationBean<FrontEndLoginFilter> frontEndLoginFilter() {
        FilterRegistrationBean<FrontEndLoginFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new FrontEndLoginFilter());
        
        // 設定攔截的URL模式
        registrationBean.addUrlPatterns(
            // 線上訂房相關
            //"/bookMulti",            多房型預定頁面
            //"/roomtype/*",           單房型預定頁面
            "/orderInfo",           // 訂單確認頁面
            
            // 帳號選物相關
//            "/front-end/shop",       商城首頁

            //"/prodCart/*",          // 購物車相關
           // "/shopOrd/*",           // 訂單相關

            
            // 會員功能相關
            "/member/center",       // 會員中心
            "/member/edit",         // 修改會員資料
            "/member/order/*",      // 會員訂單
            "/member/coupon",       // 會員折價券
            "/member/room/*",       // 會員住宿訂單
            
            // 折價券相關
            "/coupon/*/select",     // 查看折價券
            "/coupon/*/claim"       // 領取折價券
            
            //餐廳相關
            
            
        );
        
        // 設定過濾器執行順序
        registrationBean.setOrder(1);
        
        return registrationBean;
    }
} 