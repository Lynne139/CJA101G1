package com.employee.service;

import com.employee.entity.Employee;
import com.employee.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.employee.entity.Role;
import com.employee.entity.FunctionAccessRight;
import com.employee.entity.RoleAccessRight;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import com.employee.repository.RoleRepository;
import com.employee.repository.FunctionAccessRightRepository;
import com.employee.repository.RoleAccessRightRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;

@Service
public class EmployeeService {
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FunctionAccessRightRepository functionAccessRightRepository;
    @Autowired
    private RoleAccessRightRepository roleAccessRightRepository;

    // ====== 部門/權限/對應關係（僅存在於記憶體） ======
    // 已移除 roles、accessRights、roleAccessRights、roleAccessMap 相關欄位與初始化

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        return employeeRepository.findById(id).map(employee -> {
            employee.setName(employeeDetails.getName());
            // 根據你的 Employee 欄位補上其他 set
            return employeeRepository.save(employee);
        }).orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    // 取得所有部門
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
    // 新增部門
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }
    // 取得所有權限
    public List<FunctionAccessRight> getAllAccessRights() {
        return functionAccessRightRepository.findAll();
    }
    // 新增權限
    public FunctionAccessRight addAccessRight(FunctionAccessRight accessRight) {
        return functionAccessRightRepository.save(accessRight);
    }
    // 新增部門權限對應
    public RoleAccessRight addRoleAccessRight(RoleAccessRight roleAccessRight) {
        return roleAccessRightRepository.save(roleAccessRight);
    }
    // 查詢部門對應的權限
    public List<RoleAccessRight> getAccessIdsByRoleId(Integer roleId) {
        return roleAccessRightRepository.findAll().stream().filter(rar -> rar.getRoleId().equals(roleId)).toList();
    }

    // 取得權限編號對應
    public Map<Integer, String> getPermissionMap() {
        Map<Integer, String> permissionMap = new HashMap<>();
        List<FunctionAccessRight> permissions = functionAccessRightRepository.findAll();
        for (FunctionAccessRight permission : permissions) {
            permissionMap.put(permission.getAccessId(), permission.getAccessName());
        }
        return permissionMap;
    }

    // 檢查員工是否有特定權限
    public boolean hasPermission(Integer employeeId, Integer permissionId) {
        Employee employee = employeeRepository.findById(employeeId).orElse(null);
        if (employee == null) return false;
        
        // 這裡可以根據你的需求實作權限檢查邏輯
        // 例如：根據員工的 role_id 或其他欄位判斷權限
        return true; // 暫時回傳 true，你可以根據需求調整
    }
} 