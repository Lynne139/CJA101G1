package com.employee.entity;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class EmployeeFunctionAccessRightId implements Serializable {
    private Integer employeeId;
    private Integer functionAccessRightId;

    public EmployeeFunctionAccessRightId() {}
    public EmployeeFunctionAccessRightId(Integer employeeId, Integer functionAccessRightId) {
        this.employeeId = employeeId;
        this.functionAccessRightId = functionAccessRightId;
    }
    public Integer getEmployeeId() { return employeeId; }
    public void setEmployeeId(Integer employeeId) { this.employeeId = employeeId; }
    public Integer getFunctionAccessRightId() { return functionAccessRightId; }
    public void setFunctionAccessRightId(Integer functionAccessRightId) { this.functionAccessRightId = functionAccessRightId; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmployeeFunctionAccessRightId that = (EmployeeFunctionAccessRightId) o;
        return Objects.equals(employeeId, that.employeeId) &&
               Objects.equals(functionAccessRightId, that.functionAccessRightId);
    }
    @Override
    public int hashCode() {
        return Objects.hash(employeeId, functionAccessRightId);
    }
} 