package com.employee.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "employee_function_access_right")
public class EmployeeFunctionAccessRight {
    @EmbeddedId
    private EmployeeFunctionAccessRightId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("employeeId")
    @JoinColumn(name = "employee_id")
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("functionAccessRightId")
    @JoinColumn(name = "function_access_right_id")
    private FunctionAccessRight functionAccessRight;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "enabled")
    private Boolean enabled = true;

    public EmployeeFunctionAccessRight() {}

    public EmployeeFunctionAccessRight(Employee employee, FunctionAccessRight functionAccessRight, LocalDate startDate, LocalDate endDate, Boolean enabled) {
        this.id = new EmployeeFunctionAccessRightId(employee.getEmployeeId(), functionAccessRight.getAccessId());
        this.employee = employee;
        this.functionAccessRight = functionAccessRight;
        this.startDate = startDate;
        this.endDate = endDate;
        this.enabled = enabled;
    }

    public EmployeeFunctionAccessRightId getId() { return id; }
    public void setId(EmployeeFunctionAccessRightId id) { this.id = id; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public FunctionAccessRight getFunctionAccessRight() { return functionAccessRight; }
    public void setFunctionAccessRight(FunctionAccessRight functionAccessRight) { this.functionAccessRight = functionAccessRight; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
} 