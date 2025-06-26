package com.employee.service;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeDTO;
import com.employee.entity.Role;
import com.employee.entity.JobTitle;
import com.employee.entity.EmployeeFunctionAccessRight;
import com.employee.entity.FunctionAccessRight;
import com.employee.entity.RoleAccessRight;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.RoleRepository;
import com.employee.repository.JobTitleRepository;
import com.employee.repository.EmployeeFunctionAccessRightRepository;
import com.employee.repository.FunctionAccessRightRepository;
import com.employee.repository.RoleAccessRightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@Service
public class EmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private JobTitleRepository jobTitleRepository;

    @Autowired
    private EmployeeFunctionAccessRightRepository employeeFunctionAccessRightRepository;

    @Autowired
    private FunctionAccessRightRepository functionAccessRightRepository;

    @Autowired
    private RoleAccessRightRepository roleAccessRightRepository;

    // 取得所有員工（包含部門名稱和職稱名稱）
    public List<EmployeeDTO> getAllEmployeesWithDetails() {
        List<Employee> employees = employeeRepository.findAll();
        List<Role> roles = roleRepository.findAll();
        List<JobTitle> jobTitles = jobTitleRepository.findAll();
        
        // 建立角色映射
        Map<Integer, String> roleMap = new HashMap<>();
        for (Role role : roles) {
            roleMap.put(role.getRoleId(), role.getRoleName());
        }
        
        // 建立職稱映射
        Map<Integer, String> jobTitleMap = new HashMap<>();
        for (JobTitle jobTitle : jobTitles) {
            jobTitleMap.put(jobTitle.getJobTitleId(), jobTitle.getJobTitleName());
        }
        
        // 轉換為 DTO
        return employees.stream()
                .map(employee -> {
                    String roleName = roleMap.get(employee.getRoleId());
                    String jobTitleName = jobTitleMap.get(employee.getJobTitleId());
                    return new EmployeeDTO(employee, roleName, jobTitleName);
                })
                .toList();
    }

    // 取得所有員工（基本資料）
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    // 根據 ID 取得員工
    public Optional<Employee> getEmployeeById(Integer id) {
        return employeeRepository.findById(id);
    }

    // 新增員工
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }

    // 更新員工
    public Employee updateEmployee(Integer id, Employee employeeDetails) {
        return employeeRepository.findById(id)
                .map(employee -> {
                    if (employeeDetails.getName() != null) {
                        employee.setName(employeeDetails.getName());
                    }
                    if (employeeDetails.getRoleId() != null) {
                        employee.setRoleId(employeeDetails.getRoleId());
                    }
                    if (employeeDetails.getJobTitleId() != null) {
                        employee.setJobTitleId(employeeDetails.getJobTitleId());
                    }
                    if (employeeDetails.getStatus() != null) {
                        employee.setStatus(employeeDetails.getStatus());
                    }
                    return employeeRepository.save(employee);
                })
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + id));
    }

    // 取得所有部門
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    // 新增部門
    public Role addRole(Role role) {
        return roleRepository.save(role);
    }

    // 更新部門
    public Role updateRole(Integer id, Role roleDetails) {
        return roleRepository.findById(id)
                .map(role -> {
                    role.setRoleName(roleDetails.getRoleName());
                    role.setRemark(roleDetails.getRemark());
                    return roleRepository.save(role);
                })
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    // 取得所有職稱
    public List<JobTitle> getAllJobTitles() {
        return jobTitleRepository.findAll();
    }

    // 新增職稱
    public JobTitle addJobTitle(JobTitle jobTitle) {
        return jobTitleRepository.save(jobTitle);
    }

    // 更新職稱
    public JobTitle updateJobTitle(Integer id, JobTitle jobTitleDetails) {
        return jobTitleRepository.findById(id)
                .map(jobTitle -> {
                    jobTitle.setJobTitleName(jobTitleDetails.getJobTitleName());
                    jobTitle.setDescription(jobTitleDetails.getDescription());
                    return jobTitleRepository.save(jobTitle);
                })
                .orElseThrow(() -> new RuntimeException("JobTitle not found"));
    }

    // 更新員工照片
    public void updateEmployeePhoto(Integer employeeId, byte[] photoData) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        employee.setEmployeePhoto(photoData);
        employeeRepository.save(employee);
    }

    // 取得員工照片
    public byte[] getEmployeePhoto(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        return employee.getEmployeePhoto();
    }

    // 取得員工的所有權限（改為從角色權限表查）
    public List<FunctionAccessRight> getEmployeePermissions(Integer employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        Integer roleId = employee.getRoleId();
        List<RoleAccessRight> roleAccessRights = roleAccessRightRepository.findByIdRoleId(roleId);
        List<Integer> accessIds = roleAccessRights.stream()
                .map(RoleAccessRight::getAccessId)
                .toList();
        return functionAccessRightRepository.findAllById(accessIds);
    }

    // 檢查員工是否有特定權限
    public boolean hasPermission(Integer employeeId, String permissionName) {
        return getEmployeePermissions(employeeId)
                .stream()
                .anyMatch(access -> access.getAccessName().equals(permissionName));
    }

    // 檢查員工是否有任一權限
    public boolean hasAnyPermission(Integer employeeId, List<String> permissionNames) {
        return getEmployeePermissions(employeeId)
                .stream()
                .anyMatch(access -> permissionNames.contains(access.getAccessName()));
    }

    // 取得所有權限列表
    public List<FunctionAccessRight> getAllPermissions() {
        return functionAccessRightRepository.findAll();
    }

    // 根據員工ID取得權限名稱列表
    public List<String> getEmployeePermissionNames(Integer employeeId) {
        return getEmployeePermissions(employeeId)
                .stream()
                .map(FunctionAccessRight::getAccessName)
                .toList();
    }

    // 新增權限
    public FunctionAccessRight addPermission(FunctionAccessRight permission) {
        return functionAccessRightRepository.save(permission);
    }

    // 刪除權限
    public void deletePermission(Integer permissionId) {
        functionAccessRightRepository.deleteById(permissionId);
    }

    // 更新員工權限
    public void updateEmployeePermissions(Integer employeeId, List<String> permissionNames) {
        // 先刪除現有權限
        List<EmployeeFunctionAccessRight> existingPermissions = 
            employeeFunctionAccessRightRepository.findByEmployeeEmployeeIdAndEnabledTrue(employeeId);
        
        for (EmployeeFunctionAccessRight existing : existingPermissions) {
            existing.setEnabled(false);
            employeeFunctionAccessRightRepository.save(existing);
        }
        
        // 添加新權限
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
        
        for (String permissionName : permissionNames) {
            FunctionAccessRight permission = functionAccessRightRepository.findByAccessName(permissionName);
            if (permission != null) {
                EmployeeFunctionAccessRight newAccess = new EmployeeFunctionAccessRight(
                    employee, permission, java.time.LocalDate.now(), null, true);
                employeeFunctionAccessRightRepository.save(newAccess);
            }
        }
    }

    // 檢查員工是否有管理員權限
    public boolean isAdmin(Integer employeeId) {
        return hasPermission(employeeId, "ADMIN");
    }
} 