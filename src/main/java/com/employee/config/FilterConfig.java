package com.employee.config;

import com.employee.filter.AdminPermissionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

//    @Bean
//    public FilterRegistrationBean<AdminPermissionFilter> adminPermissionFilterRegistration(AdminPermissionFilter filter) {
//        FilterRegistrationBean<AdminPermissionFilter> registration = new FilterRegistrationBean<>();
//        registration.setFilter(filter);
//        registration.addUrlPatterns("/admin/*", "/employees/*");
//        registration.setOrder(1);
//        return registration;
//    }
} 