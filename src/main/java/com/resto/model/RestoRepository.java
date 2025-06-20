package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;


public interface RestoRepository extends JpaRepository<RestoVO, Integer> {
	// 查詢未刪除的
	List<RestoVO> findByIsDeletedFalse();
	// 查詢未刪除且上架的（前台用）
    List<RestoVO> findByIsDeletedFalseAndIsEnabledTrue();
    
    // 判斷阻擋餐廳重名
    List<RestoVO> findAllByRestoNameAndIsDeletedFalse(String restoName);

    
}
