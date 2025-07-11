package com.employee.filter;

import com.employee.entity.Employee;
import com.employee.entity.JobTitle;
import com.employee.repository.JobTitleRepository;
import com.employee.service.EmployeeService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Component
public class AdminPermissionFilter implements Filter {

    @Autowired
    private JobTitleRepository jobTitleRepository;
    
    @Autowired
    private EmployeeService employeeService;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession();
        String requestURI = httpRequest.getRequestURI();
        
        // 排除登入相關的 URL 和靜態資源
        if (requestURI.equals("/admin/login") || 
            requestURI.equals("/admin/doLogin") || 
            requestURI.startsWith("/admin/static/") ||
            requestURI.startsWith("/admin/css/") ||
            requestURI.startsWith("/admin/js/") ||
            requestURI.startsWith("/admin/images/") ||
            requestURI.startsWith("/css/") ||
            requestURI.startsWith("/js/") ||
            requestURI.startsWith("/images/") ||
            requestURI.startsWith("/static/")) {
            chain.doFilter(request, response);
            return;
        }
        
        // 檢查是否為後台 URL（/admin 或 /admin/* 或 /employees/*）
        boolean isAdminUrl = requestURI.equals("/admin") || 
                           requestURI.startsWith("/admin/") || 
                           requestURI.startsWith("/employees/");
        if (!isAdminUrl) {
            chain.doFilter(request, response);
            return;
        }
        
        // 第一層權限檢查：是否已登入
        Employee currentEmployee = (Employee) session.getAttribute("currentEmployee");
        if (currentEmployee == null) {
            // 如果是 AJAX 請求，返回 401 狀態碼
            String xRequestedWith = httpRequest.getHeader("X-Requested-With");
            if ("XMLHttpRequest".equals(xRequestedWith)) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
            // 否則重定向到登入頁面
            httpResponse.sendRedirect("/admin/login");
            return;
        }
        
        // 檢查員工狀態是否啟用
        if (!currentEmployee.getStatus()) {
            session.invalidate();
            httpResponse.sendRedirect("/admin/login?error=account_disabled");
            return;
        }
        
        // 查詢員工職稱
        String jobTitleName = "未知職稱";
        if (currentEmployee.getJobTitleId() != null) {
            try {
                JobTitle jobTitle = jobTitleRepository.findById(currentEmployee.getJobTitleId()).orElse(null);
                if (jobTitle != null) {
                    jobTitleName = jobTitle.getJobTitleName();
                }
            } catch (Exception e) {
                // 職稱查詢失敗，使用預設值
            }
        }
        session.setAttribute("employeeJobTitle", jobTitleName);
        
        // 使用 EmployeeService 從資料庫查詢員工權限
        Integer employeeId = currentEmployee.getEmployeeId();
        List<String> employeePermissions = employeeService.getEmployeePermissions(employeeId, LocalDate.now());
        
        // 資料庫中的權限名稱已經包含 "權限" 後綴，不需要再添加
        session.setAttribute("employeePermissions", employeePermissions);
        
        chain.doFilter(request, response);
    }
} 