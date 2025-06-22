package com.employee.controller;

import com.employee.entity.Employee;
import com.employee.entity.Role;
import com.employee.entity.FunctionAccessRight;
import com.employee.entity.RoleAccessRight;
import com.employee.service.EmployeeService;
import com.employee.repository.RoleRepository;
import com.employee.repository.FunctionAccessRightRepository;
import com.employee.repository.RoleAccessRightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private FunctionAccessRightRepository functionAccessRightRepository;

    @GetMapping
    public List<Employee> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Integer id) {
        return employeeService.getEmployeeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Employee createEmployee(@RequestBody Employee employee) {
        return employeeService.createEmployee(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Integer id, @RequestBody Employee employeeDetails) {
        try {
            Employee updated = employeeService.updateEmployee(id, employeeDetails);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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

    // 取得所有權限
    @GetMapping("/access-rights")
    public List<FunctionAccessRight> getAllAccessRights() {
        return employeeService.getAllAccessRights();
    }

    // 新增權限
    @PostMapping("/access-rights")
    public FunctionAccessRight addAccessRight(@RequestBody FunctionAccessRight accessRight) {
        return employeeService.addAccessRight(accessRight);
    }

    // 新增部門權限對應
    @PostMapping("/role-access-rights")
    public RoleAccessRight addRoleAccessRight(@RequestBody RoleAccessRight roleAccessRight) {
        return employeeService.addRoleAccessRight(roleAccessRight);
    }

    // 查詢部門對應的權限
    @GetMapping("/roles/{roleId}/access-ids")
    public List<RoleAccessRight> getAccessIdsByRoleId(@PathVariable Integer roleId) {
        return employeeService.getAccessIdsByRoleId(roleId);
    }

    // 取得權限編號對應
    @GetMapping("/permission-map")
    public Map<Integer, String> getPermissionMap() {
        return employeeService.getPermissionMap();
    }

    // 檢查員工權限
    @GetMapping("/employees/{employeeId}/has-permission/{permissionId}")
    public boolean hasPermission(@PathVariable Integer employeeId, @PathVariable Integer permissionId) {
        return employeeService.hasPermission(employeeId, permissionId);
    }

    // 編輯權限
    @PutMapping("/access-rights/{accessId}")
    public FunctionAccessRight updateAccessRight(@PathVariable Integer accessId, @RequestBody FunctionAccessRight accessRight) {
        FunctionAccessRight existing = functionAccessRightRepository.findById(accessId).orElse(null);
        if (existing != null) {
            existing.setAccessName(accessRight.getAccessName());
            return functionAccessRightRepository.save(existing);
        }
        return null;
    }
} 