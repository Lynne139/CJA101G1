package com.cs.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "customer_service_message")
public class CustomerServiceMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "message_id")
    private Integer messageId; // 留言流水號

    @Column(name = "member_id")
    private Integer memberId; // 會員編號

    @Column(name = "customer_name", length = 50)
    private String customerName; // 客戶名稱

    @Column(name = "email", nullable = false, length = 50)
    private String email; // 客戶信箱

    // submitted_at在DB中已寫DEFAULT CURRENT_TIMESTAMP，故insertable = false
    @Column(name = "submitted_at", nullable = false, insertable = false, updatable = false)
    private LocalDateTime submittedAt; // 留言時間

    @Column(name = "message", nullable = false, length = 1500)
    private String message; // 留言內容

    @Column(name = "processing_status", nullable = false)
    private Integer processingStatus = 0; // 處理狀態 (0:待回覆 1:已回覆)

    @Column(name = "employee_id")
    private Integer employeeId; // 員工編號

    @Column(name = "responsed_at")
    private LocalDateTime responsedAt; // 回覆時間

    // Getters and Setters

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        this.messageId = messageId;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
    
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }
    
    // 不需要 setter for submittedAt，讓DB自動產生

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(Integer processingStatus) {
        this.processingStatus = processingStatus;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }
    
    public LocalDateTime getResponsedAt() {
        return responsedAt;
    }

    public void setResponsedAt(LocalDateTime responsedAt) {
        this.responsedAt = responsedAt;
    }
}
