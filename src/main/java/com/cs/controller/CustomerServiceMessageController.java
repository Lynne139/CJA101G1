package com.cs.controller;

import com.cs.entity.CustomerServiceMessage;
import com.cs.service.CustomerServiceMessageService;
import com.employee.entity.Employee;
import com.util.email.CSEmailService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/cs")
public class CustomerServiceMessageController {

    @Autowired
    private CustomerServiceMessageService messageService;
    
    @Autowired
    private CSEmailService csEmailService;

    // 根據處理狀態查詢留言列表
    @GetMapping("/status/{processingStatus}")
    public ResponseEntity<List<CustomerServiceMessage>> getMessagesByStatus(@PathVariable Integer processingStatus) {
        List<CustomerServiceMessage> messages = messageService.getMessagesByProcessingStatus(processingStatus);
        return ResponseEntity.ok(messages);
    }

    // 根據 messageId 查詢單筆留言
    @GetMapping("/{messageId}")
    public ResponseEntity<CustomerServiceMessage> getMessageById(@PathVariable Integer messageId) {
        Optional<CustomerServiceMessage> messageOpt = messageService.getMessageById(messageId);
        return messageOpt.map(ResponseEntity::ok)
                         .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 更新留言處理狀態
    @PutMapping("/{messageId}/status")
    public ResponseEntity<String> updateMessageStatus(
            @PathVariable Integer messageId,
            @RequestParam Integer newStatus,
            HttpSession session) {

        Employee currentEmployee = (Employee) session.getAttribute("currentEmployee");
        if (currentEmployee == null) {
            return ResponseEntity.status(401).body("請先登入");
        }
        Integer employeeId = currentEmployee.getEmployeeId();
        boolean updated = messageService.updateMessageStatus(messageId, newStatus, employeeId);
        if (updated) {
            return ResponseEntity.ok("更新成功");
        } else {
            return ResponseEntity.status(400).body("更新失敗");
        }
    }
    
    // email 回覆客戶
    @PostMapping("/reply")
    public ResponseEntity<String> replyToCustomer(
            @RequestParam String toEmail,
            @RequestParam String customerName,
            @RequestParam String replyMessage) {

        try {
            csEmailService.sendCSEmail(toEmail, customerName, replyMessage);
            return ResponseEntity.ok("Email 已成功寄出");
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Email 發送失敗：" + e.getMessage());
        }
    }
    
    // 客戶新增一筆留言
    @PostMapping
    public ResponseEntity<CustomerServiceMessage> createMessage(@RequestBody CustomerServiceMessage message) {
        CustomerServiceMessage created = messageService.createMessage(message);
        return ResponseEntity.status(201).body(created);
    }

}
