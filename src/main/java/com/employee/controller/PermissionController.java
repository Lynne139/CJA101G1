package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.entity.FunctionAccessRight;
import com.employee.entity.EmployeeFunctionAccessRight;
import com.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/permission")
public class PermissionController {

    @Autowired
    private EmployeeService employeeService;

    // 權限管理頁面
    @GetMapping("")
    public String permissionPage(HttpServletRequest request, Model model) {
        // 獲取所有員工
        List<Employee> employees = employeeService.getAllEmployees();
        model.addAttribute("employees", employees);
        
        // 獲取所有權限
        List<FunctionAccessRight> allPermissions = employeeService.getAllPermissions();
        model.addAttribute("allPermissions", allPermissions);
        
        return "admin/fragments/staff/permission_management";
    }

    // 獲取員工權限
    @GetMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseEntity<List<String>> getEmployeePermissions(@PathVariable Integer employeeId) {
        List<String> permissions = employeeService.getEmployeePermissionNames(employeeId);
        return ResponseEntity.ok(permissions);
    }

    // 更新員工權限
    @PostMapping("/employee/{employeeId}")
    @ResponseBody
    public ResponseEntity<String> updateEmployeePermissions(
            @PathVariable Integer employeeId,
            @RequestBody List<String> permissionNames) {
        
        try {
            employeeService.updateEmployeePermissions(employeeId, permissionNames);
            return ResponseEntity.ok("權限更新成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("權限更新失敗: " + e.getMessage());
        }
    }

    // 獲取所有權限列表
    @GetMapping("/all")
    @ResponseBody
    public ResponseEntity<List<FunctionAccessRight>> getAllPermissions() {
        List<FunctionAccessRight> permissions = employeeService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    // 新增權限
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<FunctionAccessRight> addPermission(@RequestBody FunctionAccessRight permission) {
        try {
            FunctionAccessRight savedPermission = employeeService.addPermission(permission);
            return ResponseEntity.ok(savedPermission);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 刪除權限
    @DeleteMapping("/{permissionId}")
    @ResponseBody
    public ResponseEntity<String> deletePermission(@PathVariable Integer permissionId) {
        try {
            employeeService.deletePermission(permissionId);
            return ResponseEntity.ok("權限刪除成功");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("權限刪除失敗: " + e.getMessage());
        }
    }
} 