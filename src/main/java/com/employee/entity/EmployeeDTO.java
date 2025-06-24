package com.employee.entity;

import java.util.Date;

public class EmployeeDTO {
    private Integer employeeId;
    private String name;
    private Integer roleId;
    private String roleName;
    private Integer jobTitleId;
    private String jobTitleName;
    private Boolean status;
    private Date createdDate;
    private byte[] employeePhoto;

    // 建構函數
    public EmployeeDTO() {}

    public EmployeeDTO(Employee employee, String roleName, String jobTitleName) {
        this.employeeId = employee.getEmployeeId();
        this.name = employee.getName();
        this.roleId = employee.getRoleId();
        this.roleName = roleName;
        this.jobTitleId = employee.getJobTitleId();
        this.jobTitleName = jobTitleName;
        this.status = employee.getStatus();
        this.createdDate = employee.getCreatedDate();
        this.employeePhoto = employee.getEmployeePhoto();
    }

    // Getters and Setters
    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Integer getJobTitleId() {
        return jobTitleId;
    }

    public void setJobTitleId(Integer jobTitleId) {
        this.jobTitleId = jobTitleId;
    }

    public String getJobTitleName() {
        return jobTitleName;
    }

    public void setJobTitleName(String jobTitleName) {
        this.jobTitleName = jobTitleName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public byte[] getEmployeePhoto() {
        return employeePhoto;
    }

    public void setEmployeePhoto(byte[] employeePhoto) {
        this.employeePhoto = employeePhoto;
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
                "employeeId=" + employeeId +
                ", name='" + name + '\'' +
                ", roleName='" + roleName + '\'' +
                ", jobTitleName='" + jobTitleName + '\'' +
                ", status=" + status +
                ", createdDate=" + createdDate +
                '}';
    }
} 