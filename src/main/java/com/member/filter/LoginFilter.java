//package com.member.filter;
//
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.http.HttpSession;
//
//import java.io.IOException;
//
//@WebFilter(urlPatterns = {"/front-end/room", "/front-end/shop",})
//public class LoginFilter implements Filter {
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest req = (HttpServletRequest) request;
//        HttpServletResponse resp = (HttpServletResponse) response;
//        HttpSession session = req.getSession(false); // 不創建 session
//
//        boolean loggedIn = session != null && session.getAttribute("loggedInMember") != null;
//
//        if (!loggedIn) {
//            // 如果沒登入就導回首頁或登入提示頁
//            resp.sendRedirect(req.getContextPath() + "/home?forceLogin=true");
//            return;
//        }
//
//        chain.doFilter(request, response);
//    }
//}