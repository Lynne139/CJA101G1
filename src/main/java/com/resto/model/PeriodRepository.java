package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.resto.entity.PeriodVO;

public interface PeriodRepository  extends JpaRepository<PeriodVO, Integer>{
	
	// 讀取時照sort_order排
	@Query("SELECT p FROM PeriodVO p LEFT JOIN FETCH p.timeslots WHERE p.restoVO.restoId = :restoId ORDER by p.sortOrder ASC")
	List<PeriodVO> findByRestoVO_RestoIdOrderBySort(Integer restoId);
	
	PeriodVO getById(Integer periodId);
	
	// 判斷阻擋區段重名(只找同一餐廳、同名的區段)
    List<PeriodVO> findAllByRestoVO_RestoIdAndPeriodName(Integer restoId, String periodName);
    
    // 排序用
    // 取某餐廳該區段排序最大值
    @Query("SELECT COALESCE(max(p.sortOrder),0) FROM PeriodVO p WHERE p.restoVO.restoId = :restoId")
    Integer findMaxSortOrderByRestoId(Integer restoId);
    
    // 找上面一筆
    Optional<PeriodVO> 
    findTopByRestoVO_RestoIdAndSortOrderLessThanOrderBySortOrderDesc(
            Integer restoId, Integer sortOrder);

    // 找下面一筆
    Optional<PeriodVO> 
    findTopByRestoVO_RestoIdAndSortOrderGreaterThanOrderBySortOrderAsc(
            Integer restoId, Integer sortOrder);


}
