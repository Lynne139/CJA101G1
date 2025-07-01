package com.employee.repository;

import com.employee.entity.EmployeeFunctionAccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmployeeFunctionAccessRightRepository extends JpaRepository<EmployeeFunctionAccessRight, Integer> {
    
    @Query("SELECT efar FROM EmployeeFunctionAccessRight efar " +
           "WHERE efar.employee.employeeId = :employeeId " +
           "AND (efar.startDate IS NULL OR efar.startDate <= :checkDate) " +
           "AND (efar.endDate IS NULL OR efar.endDate >= :checkDate) " +
           "AND efar.enabled = true")
    List<EmployeeFunctionAccessRight> findByEmployeeIdAndDateRange(
            @Param("employeeId") Integer employeeId, 
            @Param("checkDate") LocalDate checkDate);
} 