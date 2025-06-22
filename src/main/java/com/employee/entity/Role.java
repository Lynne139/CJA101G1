package com.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "role_list")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Role_id")
    private Integer roleId;

    @Column(name = "Role_name", nullable = false, length = 50)
    private String roleName;

    @Column(name = "Remark", length = 100)
    private String remark;

    @Column(name = "Is_active", nullable = false)
    private Boolean isActive = true;

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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
} 