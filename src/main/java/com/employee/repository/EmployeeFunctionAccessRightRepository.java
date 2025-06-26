package com.employee.repository;

import com.employee.entity.EmployeeFunctionAccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeFunctionAccessRightRepository extends JpaRepository<EmployeeFunctionAccessRight, Integer> {
    
    // 根據員工ID查詢所有啟用的權限
    @Query("SELECT efar FROM EmployeeFunctionAccessRight efar " +
           "WHERE efar.employee.employeeId = :employeeId AND efar.enabled = true")
    List<EmployeeFunctionAccessRight> findByEmployeeEmployeeIdAndEnabledTrue(@Param("employeeId") Integer employeeId);
    
    // 根據員工ID和權限名稱查詢
    @Query("SELECT efar FROM EmployeeFunctionAccessRight efar " +
           "WHERE efar.employee.employeeId = :employeeId " +
           "AND efar.functionAccessRight.accessName = :accessName " +
           "AND efar.enabled = true")
    List<EmployeeFunctionAccessRight> findByEmployeeIdAndAccessName(@Param("employeeId") Integer employeeId, 
                                                                   @Param("accessName") String accessName);
} 