package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.entity.FunctionAccessRight;
import com.employee.entity.EmployeeFunctionAccessRight;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.FunctionAccessRightRepository;
import com.employee.repository.EmployeeFunctionAccessRightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class EmployeeAccessRightController {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private FunctionAccessRightRepository functionAccessRightRepository;
    @Autowired
    private EmployeeFunctionAccessRightRepository employeeFunctionAccessRightRepository;

    // 員工列表
    @GetMapping("/staff3")
    public String listEmployees(Model model, HttpServletRequest request) {
        List<Employee> employees = employeeRepository.findAll();
        model.addAttribute("employees", employees);
        model.addAttribute("mainFragment", "admin/fragments/staff/staff3");
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    }

    // 編輯權限頁面
    @GetMapping("/employee/{id}/edit-rights")
    public String editRights(@PathVariable("id") Integer id, Model model) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        List<FunctionAccessRight> allRights = functionAccessRightRepository.findAll();
        List<EmployeeFunctionAccessRight> empRights = employeeFunctionAccessRightRepository.findByEmployeeIdAndDateRange(id, LocalDate.now());
        Set<Integer> enabledRightIds = empRights.stream()
                .filter(EmployeeFunctionAccessRight::getEnabled)
                .map(e -> e.getFunctionAccessRight().getAccessId())
                .collect(Collectors.toSet());
        model.addAttribute("employee", employee);
        model.addAttribute("allRights", allRights);
        model.addAttribute("enabledRightIds", enabledRightIds);
        return "admin/fragments/staff/edit-rights";
    }

    // 權限儲存
    @PostMapping("/employee/{id}/edit-rights")
    public String saveRights(@PathVariable("id") Integer id, @RequestParam(value = "rights", required = false) List<Integer> rights) {
        List<FunctionAccessRight> allRights = functionAccessRightRepository.findAll();
        List<EmployeeFunctionAccessRight> empRights = employeeFunctionAccessRightRepository.findByEmployeeIdAndDateRange(id, LocalDate.now());
        Set<Integer> selected = rights == null ? new HashSet<>() : new HashSet<>(rights);
        // 先全部設為 disabled
        for (EmployeeFunctionAccessRight efar : empRights) {
            efar.setEnabled(false);
            employeeFunctionAccessRightRepository.save(efar);
        }
        // 再針對勾選的設為 enabled，若無紀錄則新增
        for (FunctionAccessRight right : allRights) {
            if (selected.contains(right.getAccessId())) {
                Optional<EmployeeFunctionAccessRight> exist = empRights.stream()
                        .filter(e -> e.getFunctionAccessRight().getAccessId().equals(right.getAccessId()))
                        .findFirst();
                if (exist.isPresent()) {
                    exist.get().setEnabled(true);
                    employeeFunctionAccessRightRepository.save(exist.get());
                } else {
                    EmployeeFunctionAccessRight newRight = new EmployeeFunctionAccessRight();
                    newRight.setId(new com.employee.entity.EmployeeFunctionAccessRightId(id, right.getAccessId()));
                    newRight.setEmployee(employeeRepository.findById(id).orElse(null));
                    newRight.setFunctionAccessRight(right);
                    newRight.setStartDate(LocalDate.now());
                    newRight.setEnabled(true);
                    employeeFunctionAccessRightRepository.save(newRight);
                }
            }
        }
        return "redirect:/admin/staff3";
    }

    // 權限編輯 Modal 載入資料 API
    @GetMapping("/employee/{id}/rights-json")
    @ResponseBody
    public Map<String, Object> getEmployeeRightsJson(@PathVariable("id") Integer id) {
        List<FunctionAccessRight> allRights = functionAccessRightRepository.findAll();
        List<EmployeeFunctionAccessRight> empRights = employeeFunctionAccessRightRepository.findByEmployeeIdAndDateRange(id, java.time.LocalDate.now());
        Set<Integer> enabledRightIds = empRights.stream()
                .filter(EmployeeFunctionAccessRight::getEnabled)
                .map(e -> e.getFunctionAccessRight().getAccessId())
                .collect(java.util.stream.Collectors.toSet());
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("allRights", allRights);
        result.put("enabledRightIds", enabledRightIds);
        return result;
    }
} 