package com.employee.service;

import com.employee.entity.Employee;
import com.employee.entity.EmployeeDTO;
import com.employee.entity.Role;
import com.employee.entity.JobTitle;
import com.employee.entity.EmployeeFunctionAccessRight;
import com.employee.entity.FunctionAccessRight;
import com.employee.repository.EmployeeRepository;
import com.employee.repository.RoleRepository;
import com.employee.repository.JobTitleRepository;
import com.employee.repository.EmployeeFunctionAccessRightRepository;
import com.employee.repository.FunctionAccessRightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
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
                    // 總是更新 roleId 和 jobTitleId，允許為 null
                    employee.setRoleId(employeeDetails.getRoleId());
                    employee.setJobTitleId(employeeDetails.getJobTitleId());
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

    // 根據員工ID和日期取得權限列表
    @Transactional(readOnly = true)
    public List<String> getEmployeePermissions(Integer employeeId, LocalDate checkDate) {
        List<EmployeeFunctionAccessRight> accessRights = employeeFunctionAccessRightRepository
                .findByEmployeeIdAndDateRange(employeeId, checkDate);
        
        return accessRights.stream()
                .filter(access -> access.getEnabled())
                .map(access -> access.getFunctionAccessRight().getAccessName())
                .collect(Collectors.toList());
    }
    
    // 檢查員工是否有特定權限
    @Transactional(readOnly = true)
    public boolean hasPermission(Integer employeeId, String permissionName, LocalDate checkDate) {
        List<String> permissions = getEmployeePermissions(employeeId, checkDate);
        return permissions.contains(permissionName);
    }
    
    // 員工登入驗證（使用 "maison" + employeeId 格式）
    public Employee login(String username, String password) {
        if (!username.startsWith("maison")) {
            return null;
        }
        
        try {
            String employeeIdStr = username.substring(6); // 移除 "maison" 前綴
            Integer employeeId = Integer.parseInt(employeeIdStr);
            
            Optional<Employee> employeeOpt = employeeRepository.findById(employeeId);
            if (employeeOpt.isPresent()) {
                Employee employee = employeeOpt.get();
                if (employee.getPassword().equals(password) && employee.getStatus()) {
                    return employee;
                }
            }
        } catch (NumberFormatException e) {
            // 員工ID格式錯誤
        }
        
        return null;
    }
} 