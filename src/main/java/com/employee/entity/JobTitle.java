package com.employee.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "job_title")
public class JobTitle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_title_id")
    private Integer jobTitleId;

    @NotBlank(message = "職稱名稱不能為空")
    @Pattern(regexp = "^[\u4e00-\u9fff\\s]+$", message = "職稱名稱只能包含中文字符")
    @Size(max = 50, message = "職稱名稱長度不能超過50個字符")
    @Column(name = "job_title_name", nullable = false, length = 50)
    private String jobTitleName;

    @Size(max = 200, message = "職稱說明長度不能超過200個字符")
    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
} 