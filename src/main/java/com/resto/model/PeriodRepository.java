package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.entity.PeriodVO;
import com.resto.integration.room.RestoPeriodCode;

@Repository
public interface PeriodRepository  extends JpaRepository<PeriodVO, Integer>{
	
	// 讀取時照sort_order排
	@Query("SELECT p FROM PeriodVO p LEFT JOIN FETCH p.timeslots WHERE p.restoVO.restoId = :restoId ORDER by p.sortOrder ASC")
	List<PeriodVO> findByRestoVO_RestoIdOrderBySort(Integer restoId);
	
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
    
    
    // ===== 住宿訂單相關 =====
    // 依餐廳 + code 找到 period
    Optional<PeriodVO> findByRestoVO_RestoIdAndPeriodCode(Integer restoId, RestoPeriodCode code);


    @Modifying(clearAutomatically = true)
    @Query("UPDATE PeriodVO p SET p.periodCode = NULL " +
           "WHERE p.periodId = :pid AND p.restoVO.restoId = :rid")
    int clearPeriodCode(@Param("pid") Integer pid,
                        @Param("rid") Integer rid);
    
    
    // Perod+Timeslot+Resto
    @Query("""
            SELECT DISTINCT p
            FROM PeriodVO p
            JOIN FETCH p.restoVO r
            LEFT JOIN FETCH p.timeslots t
            WHERE p.periodCode = :code
              AND r.isDeleted = false
            ORDER BY r.restoId, p.sortOrder, t.timeslotName
            """)
        List<PeriodVO> findByPeriodCodeWithRestoAndTimeslots(@Param("code") RestoPeriodCode code);

	    // 抓指定餐廳(上架)的 period，並一次把 timeslot 一併載入  
	    @Query("""
	            select distinct p
	            from PeriodVO p
	            left join fetch p.timeslots ts
	            where p.restoVO.restoId = :restoId
	              and p.restoVO.isDeleted = false
	              and p.restoVO.isEnabled = true
	            order by p.sortOrder asc, ts.timeslotId asc
	            """)
	     List<PeriodVO> findByRestoIdWithSlots(Integer restoId);
	    
    
    
    
    
    
    
}
