package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class LoginController {

    @Autowired
    private EmployeeService employeeService;

    // 登入頁面
    @GetMapping("/login")
    public String loginPage() {
        return "admin/login";
    }

    // 處理登入
    @PostMapping("/login")
    public String login(@RequestParam String employeeId, 
                       @RequestParam String password,
                       HttpSession session,
                       RedirectAttributes redirectAttributes) {
        
        try {
            Integer empId = Integer.parseInt(employeeId);
            Optional<Employee> employeeOpt = employeeService.getEmployeeById(empId);
            
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                
                // 驗證密碼和狀態
                if (password.equals(employee.getPassword()) && employee.getStatus()) {
                    // 登入成功，將員工資訊存入session
                    session.setAttribute("adminEmployee", employee);
                    session.setAttribute("adminEmployeeId", employee.getEmployeeId());
                    session.setAttribute("adminEmployeeName", employee.getName());
                    
                    return "redirect:/admin";
                } else {
                    redirectAttributes.addFlashAttribute("error", "密碼錯誤或帳號已停用");
                }
            } else {
                redirectAttributes.addFlashAttribute("error", "員工編號不存在");
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("error", "員工編號格式錯誤");
        }
        
        return "redirect:/admin/login";
    }

    // 登出
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/admin/login";
    }

    // 檢查登入狀態
    @GetMapping("/check-login")
    @ResponseBody
    public boolean checkLogin(HttpSession session) {
        return session.getAttribute("adminEmployee") != null;
    }
}
