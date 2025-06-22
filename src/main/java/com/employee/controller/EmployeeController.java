package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeDTO;
import com.employee.entity.Role;
import com.employee.entity.JobTitle;
import com.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    
    @Autowired
    private EmployeeService employeeService;

    // 取得所有員工（包含部門名稱和職稱名稱）
    @GetMapping("/with-details")
    public List<EmployeeDTO> getAllEmployeesWithDetails() {
        return employeeService.getAllEmployeesWithDetails();
    }

    // 取得所有員工（基本資料）
    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    // 根據 ID 取得員工
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        return employee.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 新增員工
    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    // 更新員工
    @PutMapping("/{id}")
    public ResponseEntity<String> updateEmployee(@PathVariable Integer id, @RequestBody Employee employeeDetails) {
        try {
            Employee updated = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok("員工資料更新成功");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("員工不存在");
        }
    }

    // 上傳員工照片
    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadEmployeePhoto(@PathVariable Integer id, @RequestParam("photo") MultipartFile photo) {
        try {
            if (photo.isEmpty()) {
                return ResponseEntity.badRequest().body("請選擇照片檔案");
            }
            
            // 檢查檔案大小 (限制為 10MB)
            if (photo.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("檔案大小不能超過 10MB");
            }
            
            // 檢查檔案類型
            String contentType = photo.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body("只能上傳圖片檔案");
            }
            
            byte[] photoData = photo.getBytes();
            employeeService.updateEmployeePhoto(id, photoData);
            return ResponseEntity.ok("員工照片上傳成功");
        } catch (Exception e) {
            e.printStackTrace(); // 在控制台輸出詳細錯誤
            return ResponseEntity.badRequest().body("上傳失敗: " + e.getMessage());
        }
    }

    // 取得員工照片
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getEmployeePhoto(@PathVariable Integer id) {
        try {
            byte[] photoData = employeeService.getEmployeePhoto(id);
            if (photoData != null && photoData.length > 0) {
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                return new ResponseEntity<>(photoData, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====== 部門管理 ======
    
    // 取得所有部門
    @GetMapping("/roles")
    public List<Role> getAllRoles() {
        return employeeService.getAllRoles();
    }

    // 新增部門
    @PostMapping("/roles")
    public Role addRole(@RequestBody Role role) {
        return employeeService.addRole(role);
    }

    // 更新部門
    @PutMapping("/roles/{id}")
    public ResponseEntity<Role> updateRole(@PathVariable Integer id, @RequestBody Role roleDetails) {
        try {
            Role updated = employeeService.updateRole(id, roleDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ====== 職稱管理 ======
    
    // 取得所有職稱
    @GetMapping("/job-titles")
    public List<JobTitle> getAllJobTitles() {
        return employeeService.getAllJobTitles();
    }

    // 新增職稱
    @PostMapping("/job-titles")
    public JobTitle addJobTitle(@RequestBody JobTitle jobTitle) {
        return employeeService.addJobTitle(jobTitle);
    }

    // 更新職稱
    @PutMapping("/job-titles/{id}")
    public ResponseEntity<JobTitle> updateJobTitle(@PathVariable Integer id, @RequestBody JobTitle jobTitleDetails) {
        try {
            JobTitle updated = employeeService.updateJobTitle(id, jobTitleDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 