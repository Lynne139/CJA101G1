package com.employee.config;

import com.employee.entity.EmployeeFunctionAccessRight;
import com.employee.repository.EmployeeFunctionAccessRightRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class EmployeePermissionInterceptor implements HandlerInterceptor {
    @Autowired
    private EmployeeFunctionAccessRightRepository employeeFunctionAccessRightRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 假設 session 有 currentEmployeeId
        Integer empId = (Integer) request.getSession().getAttribute("currentEmployeeId");
        if (empId != null) {
            List<EmployeeFunctionAccessRight> rights = employeeFunctionAccessRightRepository.findByEmployeeIdAndDateRange(empId, LocalDate.now());
            Set<String> permissionNames = rights.stream()
                .filter(EmployeeFunctionAccessRight::getEnabled)
                .map(r -> r.getFunctionAccessRight().getAccessName() + "權限")
                .collect(Collectors.toSet());
            request.getSession().setAttribute("employeePermissions", permissionNames);
        }
        return true;
    }
} 