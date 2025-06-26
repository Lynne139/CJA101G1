package com.employee.filter;

import com.employee.entity.Employee;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
// @WebFilter(urlPatterns = {"/admin/*"})
@Order(1)
public class AdminAuthenticationFilter implements Filter {

    // 不需要登入驗證的路徑
    private static final String[] EXCLUDED_PATHS = {
        "/admin/login",
        "/admin/logout",
        "/css",
        "/images",
        "/js",
        "/favicon.ico"
    };

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        // 暫時註解掉登入驗證，方便開發測試
        chain.doFilter(request, response);
        return;
        
        /*
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false);
        
        String requestURI = httpRequest.getRequestURI();
        
        // 靜態資源副檔名直接放行
        if (requestURI.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico)$")) {
            chain.doFilter(request, response);
            return;
        }
        // 檢查是否為排除的路徑
        if (isExcludedPath(requestURI)) {
            chain.doFilter(request, response);
            return;
        }
        
        // 檢查是否已登入
        if (session != null && session.getAttribute("adminEmployee") != null) {
            Employee employee = (Employee) session.getAttribute("adminEmployee");
            
            // 檢查員工狀態是否為啟用
            if (employee.getStatus()) {
                chain.doFilter(request, response);
                return;
            } else {
                // 員工帳號已停用
                session.invalidate();
                httpResponse.sendRedirect("/admin/login?error=account_disabled");
                return;
            }
        }
        
        // 未登入，重定向到登入頁面
        httpResponse.sendRedirect("/admin/login");
        */
    }

    private boolean isExcludedPath(String requestURI) {
        for (String excludedPath : EXCLUDED_PATHS) {
            if (requestURI.startsWith(excludedPath)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 初始化
    }

    @Override
    public void destroy() {
        // 清理資源
    }
} 