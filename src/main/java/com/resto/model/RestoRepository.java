package com.resto.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.resto.dto.RestoDTO;
import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;

@Repository
public interface RestoRepository extends JpaRepository<RestoVO, Integer> {
	// 查詢未刪除的
	List<RestoVO> findByIsDeletedFalse();
	// 查詢未刪除且上架的（前台用）
    List<RestoVO> findByIsDeletedFalseAndIsEnabledTrue();
    
    // 判斷阻擋餐廳重名
    List<RestoVO> findAllByRestoNameAndIsDeletedFalse(String restoName);
    
    //DTO
    @Query("SELECT new com.resto.dto.RestoDTO(r.restoId, r.restoName, r.restoNameEn, r.restoLoc, r.restoSeatsTotal, r.isEnabled) " +
    	       "FROM RestoVO r WHERE r.isDeleted = false")
    List<RestoDTO> findAllDTO();

    // 查單筆給前台(必須上架且非軟刪)
    Optional<RestoVO> findByRestoIdAndIsDeletedFalseAndIsEnabledTrue(Integer id);
    
    
    
    
    

    
    
    
    
    
}
