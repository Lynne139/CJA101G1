package com.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "role_list")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Role_id")
    private Integer roleId;

    @NotBlank(message = "部門名稱不能為空")
    @Pattern(regexp = "^[\u4e00-\u9fff\\s]+$", message = "部門名稱只能包含中文字符")
    @Size(max = 50, message = "部門名稱長度不能超過50個字符")
    @Column(name = "Role_name", nullable = false, length = 50)
    private String roleName;

    @Size(max = 100, message = "備註長度不能超過100個字符")
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