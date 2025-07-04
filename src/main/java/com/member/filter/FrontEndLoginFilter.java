package com.member.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import com.member.model.MemberVO;

import java.io.IOException;

/**
 * 前台登入過濾器
 * 攔截需要登入才能使用的三大功能：線上訂房、帳號選物、餐廳訂位
 */
public class FrontEndLoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 檢查是否已登入
        boolean loggedIn = session != null && session.getAttribute("loggedInMember") != null;

        if (!loggedIn) {
            // 未登入，根據請求類型返回不同回應
            String requestedWith = req.getHeader("X-Requested-With");
            
            if ("XMLHttpRequest".equals(requestedWith)) {
                // AJAX 請求，返回 JSON 錯誤訊息
                resp.setContentType("application/json;charset=UTF-8");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"success\": false, \"message\": \"請先登入會員\", \"loginRequired\": true}");
                return;
            } else {
                // 一般頁面請求，重定向到首頁並彈出登入Modal
                String contextPath = req.getContextPath();
                resp.sendRedirect(contextPath + "/home?loginRequired=true");
                return;
            }
        }

        // 已登入，繼續執行
        chain.doFilter(request, response);
    }
} 