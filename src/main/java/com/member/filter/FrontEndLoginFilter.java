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
                // 一般頁面請求，返回包含登入Modal的HTML
                resp.setContentType("text/html;charset=UTF-8");
                resp.getWriter().write(getLoginRequiredHtml());
                return;
            }
        }

        // 已登入，繼續執行
        chain.doFilter(request, response);
    }
    
    /**
     * 生成包含登入Modal的HTML頁面
     */
    private String getLoginRequiredHtml() {
        return """
            <!DOCTYPE html>
            <html lang="zh-Hant">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>請先登入 - Maison d'Yuko</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
                <style>
                    body { 
                        background-color: #f8f9fa; 
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                    }
                    .login-container {
                        min-height: 100vh;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    }
                    .login-card {
                        background: white;
                        border-radius: 15px;
                        box-shadow: 0 10px 30px rgba(0,0,0,0.1);
                        padding: 2rem;
                        width: 100%;
                        max-width: 400px;
                    }
                    .brand-logo {
                        text-align: center;
                        margin-bottom: 1.5rem;
                    }
                    .brand-title {
                        color: #8B4513;
                        font-size: 1.5rem;
                        font-weight: bold;
                        margin-bottom: 0.5rem;
                    }
                    .login-message {
                        text-align: center;
                        color: #dc3545;
                        margin-bottom: 1.5rem;
                        font-size: 0.9rem;
                    }
                    .form-control {
                        border-radius: 8px;
                        border: 1px solid #ddd;
                        padding: 0.7rem;
                        margin-bottom: 1rem;
                    }
                    .btn-login {
                        background-color: #8B4513;
                        border: none;
                        border-radius: 8px;
                        padding: 0.7rem;
                        width: 100%;
                        color: white;
                        font-weight: bold;
                        transition: background-color 0.3s;
                    }
                    .btn-login:hover {
                        background-color: #6B3410;
                    }
                    .back-link {
                        text-align: center;
                        margin-top: 1rem;
                    }
                    .back-link a {
                        color: #8B4513;
                        text-decoration: none;
                    }
                    .back-link a:hover {
                        text-decoration: underline;
                    }
                </style>
            </head>
            <body>
                <div class="login-container">
                    <div class="login-card">
                        <div class="brand-logo">
                            <div class="brand-title">Maison d'Yuko</div>
                            <div style="color: #666; font-size: 0.9rem;">嶼蔻渡假村</div>
                        </div>
                        
                        <div class="login-message">
                            <i class="fas fa-lock"></i>
                            請先登入會員才能使用此功能
                        </div>
                        
                        <form id="loginForm">
                            <div class="mb-3">
                                <input type="email" class="form-control" name="memberEmail" placeholder="請輸入信箱" required>
                            </div>
                            <div class="mb-3">
                                <input type="password" class="form-control" name="memberPassword" placeholder="請輸入密碼" required>
                            </div>
                            <div id="loginError" class="text-danger text-center mb-3" style="display: none;"></div>
                            <button type="submit" class="btn btn-login">登入</button>
                        </form>
                        
                        <div class="back-link">
                            <a href="javascript:history.back()">← 返回上一頁</a>
                        </div>
                    </div>
                </div>
                
                <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
                <script>
                    document.getElementById('loginForm').addEventListener('submit', function(e) {
                        e.preventDefault();
                        
                        const formData = new FormData(this);
                        const errorDiv = document.getElementById('loginError');
                        
                        fetch('/member/ajaxLogin', {
                            method: 'POST',
                            body: formData
                        })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                // 登入成功，重新載入當前頁面
                                window.location.reload();
                            } else {
                                errorDiv.textContent = data.message || '登入失敗';
                                errorDiv.style.display = 'block';
                            }
                        })
                        .catch(error => {
                            errorDiv.textContent = '登入發生錯誤，請稍後再試';
                            errorDiv.style.display = 'block';
                        });
                    });
                </script>
            </body>
            </html>
            """;
    }
} 