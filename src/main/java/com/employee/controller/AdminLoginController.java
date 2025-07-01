package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.service.EmployeeService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    @Autowired
    private EmployeeService employeeService;

    // 顯示登入頁面
    @GetMapping("/login")
    public String showLoginPage(HttpServletRequest request, Model model) {
        String error = request.getParameter("error");
        if (error != null) {
            switch (error) {
                case "invalid_credentials":
                    model.addAttribute("errorMessage", "帳號或密碼錯誤");
                    break;
                case "account_disabled":
                    model.addAttribute("errorMessage", "帳號已被停用");
                    break;
                default:
                    model.addAttribute("errorMessage", "登入失敗");
            }
        }
        return "admin/login";
    }

    // 處理登入
    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                         @RequestParam("password") String password,
                         HttpServletRequest request,
                         HttpServletResponse response,
                         Model model) {
        
        // 驗證登入
        Employee employee = employeeService.login(username, password);
        
        if (employee != null) {
            // 登入成功，將員工資訊存入 session
            HttpSession session = request.getSession();
            session.setAttribute("currentEmployee", employee);
            
            // 重定向到後台首頁
            return "redirect:/admin";
        } else {
            // 登入失敗
            return "redirect:/admin/login?error=invalid_credentials";
        }
    }

    // 登出
    @GetMapping("/logout")
    public String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "redirect:/admin/login";
    }
} 