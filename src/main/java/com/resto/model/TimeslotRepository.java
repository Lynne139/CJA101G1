package com.resto.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeslotRepository  extends JpaRepository<TimeslotVO, Integer>{
	
	List<TimeslotVO> findByRestoVO_RestoId(Integer restoId);



}
