package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.resto.entity.PeriodVO;

public interface PeriodRepository  extends JpaRepository<PeriodVO, Integer>{
	
	// 讀取時照sort_order排
	@Query("SELECT p FROM PeriodVO p LEFT JOIN FETCH p.timeslots t WHERE p.restoVO.restoId = :restoId AND p.isDeleted = false ORDER by p.sortOrder ASC")
	List<PeriodVO> findActiveByRestoId(Integer restoId);
	
	PeriodVO getById(Integer periodId);
	
	// 判斷阻擋區段重名(只找同一餐廳、同名的區段)
    List<PeriodVO> findAllByRestoVO_RestoIdAndPeriodNameAndIsDeletedFalse(Integer restoId, String periodName);
    
    // 排序用
    // 取某餐廳該區段排序最大值
    @Query("SELECT COALESCE(max(p.sortOrder),0) FROM PeriodVO p WHERE p.restoVO.restoId = :restoId AND  p.isDeleted = false")
    Integer findMaxSortOrderByRestoId(Integer restoId);
    
    // 找上面一筆
    Optional<PeriodVO> 
    findTopByRestoVO_RestoIdAndSortOrderLessThanAndIsDeletedFalseOrderBySortOrderDesc(
            Integer restoId, Integer sortOrder);

    // 找下面一筆
    Optional<PeriodVO> 
    findTopByRestoVO_RestoIdAndSortOrderGreaterThanAndIsDeletedFalseOrderBySortOrderAsc(
            Integer restoId, Integer sortOrder);


}
