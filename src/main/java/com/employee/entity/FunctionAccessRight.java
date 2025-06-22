package com.employee.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "function_access_right")
public class FunctionAccessRight {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Access_id")
    private Integer accessId;

    @Column(name = "Access_name", nullable = false, length = 50)
    private String accessName;

    public Integer getAccessId() {
        return accessId;
    }

    public void setAccessId(Integer accessId) {
        this.accessId = accessId;
    }

    public String getAccessName() {
        return accessName;
    }

    public void setAccessName(String accessName) {
        this.accessName = accessName;
    }
} 