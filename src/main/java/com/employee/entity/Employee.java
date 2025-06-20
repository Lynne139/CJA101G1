package com.employee.entity;

import java.util.Date;
import jakarta.persistence.*;

@Entity
@Table(name = "employee")
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Employee_id")
    private Integer employeeId;

    @Column(name = "Role_id", nullable = false)
    private Integer roleId;

    @Column(name = "Name", nullable = false, length = 50)
    private String name;

    @Column(name = "Status", nullable = false)
    private Boolean status = true;

    @Column(name = "Created_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Column(name = "Password", nullable = false, length = 50)
    private String password;

    @Column(name = "job", length = 50)
    private String job;

    @Column(name = "department", length = 50)
    private String department;

    @Lob
    @Column(name = "employee_photo")
    private byte[] employeePhoto;

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public byte[] getEmployeePhoto() {
        return employeePhoto;
    }

    public void setEmployeePhoto(byte[] employeePhoto) {
        this.employeePhoto = employeePhoto;
    }
}
