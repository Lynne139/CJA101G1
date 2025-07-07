package com.cs.service;

import com.cs.entity.CustomerServiceMessage;
import com.cs.repository.CustomerServiceMessageRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CustomerServiceMessageService {

    @Autowired
    private CustomerServiceMessageRepository messageRepository;

    // 根據處理狀態查詢，排序
    public List<CustomerServiceMessage> getMessagesByProcessingStatus(Integer processingStatus) {
        return messageRepository.findByProcessingStatusOrderByMessageIdAsc(processingStatus);
    }
    
    // 依 messageId 查詢單筆留言
    public Optional<CustomerServiceMessage> getMessageById(Integer messageId) {
        return messageRepository.findById(messageId);
    }
    
    // 依 messageId 更新處理狀態、回覆人員、回覆時間
    public boolean updateMessageStatus(Integer messageId, Integer newStatus, Integer employeeId) {
        Optional<CustomerServiceMessage> optionalMessage = messageRepository.findById(messageId);
        if (optionalMessage.isPresent()) {
            CustomerServiceMessage message = optionalMessage.get();
            message.setProcessingStatus(newStatus);
            message.setEmployeeId(employeeId);
            message.setResponsedAt(LocalDateTime.now());
            messageRepository.save(message);
            return true;
        }
        return false;
    }
    
    // 新增一筆留言
    public CustomerServiceMessage createMessage(CustomerServiceMessage message) {
        return messageRepository.save(message);
    }
}
