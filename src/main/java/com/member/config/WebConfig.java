//package com.member.config;
//
//import org.springframework.boot.web.servlet.FilterRegistrationBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.member.filter.LoginFilter;
//
//@Configuration
//public class WebConfig {
//
//    @Bean
//    public FilterRegistrationBean<LoginFilter> loginFilter() {
//        FilterRegistrationBean<LoginFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(new LoginFilter());
//        registration.addUrlPatterns("/front-end/room", "/front-end/shop");
//        return registration;
//    }
//}
