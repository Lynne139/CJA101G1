package com.resto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository  extends JpaRepository<PeriodVO, Integer>{
	
	List<PeriodVO> findByRestoVO_RestoId(Integer restoId);


}
