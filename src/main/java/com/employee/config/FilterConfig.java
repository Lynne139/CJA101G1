package com.employee.config;

import com.employee.filter.AdminPermissionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
public class FilterConfig implements WebMvcConfigurer {

    // 註解掉 Interceptor 以避免與 Filter 的權限設定產生衝突
    // @Autowired
    // private com.employee.config.EmployeePermissionInterceptor employeePermissionInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 註解掉 Interceptor 註冊，改用 Filter 統一處理權限
        // registry.addInterceptor(employeePermissionInterceptor)
        //         .addPathPatterns("/admin/**");
    }

    @Bean
    public FilterRegistrationBean<AdminPermissionFilter> adminPermissionFilterRegistration(AdminPermissionFilter filter) {
        FilterRegistrationBean<AdminPermissionFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/admin/*", "/employees/*");
        registration.setOrder(1);
        return registration;
    }
} 