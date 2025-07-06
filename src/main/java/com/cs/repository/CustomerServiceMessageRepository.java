package com.cs.repository;

import com.cs.entity.CustomerServiceMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CustomerServiceMessageRepository extends JpaRepository<CustomerServiceMessage, Integer> {
    
	// 根據 processingStatus 查詢，並依 messageId 升冪排序
    List<CustomerServiceMessage> findByProcessingStatusOrderByMessageIdAsc(Integer processingStatus);
}
