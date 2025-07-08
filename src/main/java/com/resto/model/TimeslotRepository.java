package com.resto.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.resto.entity.TimeslotVO;

@Repository
public interface TimeslotRepository  extends JpaRepository<TimeslotVO, Integer>{
	
	// 取未刪除時段，順便抓period
	@Query("SELECT t FROM TimeslotVO t JOIN FETCH t.periodVO WHERE t.restoVO.restoId = :restoId AND  t.isDeleted = false ORDER BY t.timeslotName ASC")
	List<TimeslotVO> findActiveWithPeriodByResto(@Param("restoId") Integer restoId);
	
	// 餐廳下所有未刪除時段，依名稱排序
    List<TimeslotVO> findByRestoVO_RestoIdAndIsDeletedFalseOrderByTimeslotNameAsc(Integer restoId);

    // 重名檢查(未刪除)
    List<TimeslotVO> findByRestoVO_RestoIdAndTimeslotNameAndIsDeletedFalse(Integer restoId, String timeslotName);

    // 檢查是否有已刪除同名（復原）
    Optional<TimeslotVO> findByRestoVO_RestoIdAndTimeslotNameAndIsDeletedTrue(Integer restoId, String timeslotName);

    // 抓單筆未軟刪
    Optional<TimeslotVO> findByTimeslotIdAndIsDeletedFalse(Integer timeslotId);
    
    

    //拿未刪多筆(給order add modal用)
    @Query("SELECT t FROM TimeslotVO t JOIN FETCH t.periodVO WHERE t.isDeleted = false ORDER BY t.timeslotName")
    List<TimeslotVO> findActiveWithPeriod();

    
    
    // 依餐廳取出（含 isEnabled 篩選、軟刪除過濾）
    @Query("""
        SELECT t
        FROM TimeslotVO t
        WHERE t.periodVO.restoVO.restoId = :restoId
          AND t.isDeleted = false
        ORDER BY t.timeslotId
    """)
    List<TimeslotVO> findByResto(Integer restoId);


    
    
    
    
    
    
    
    
}
