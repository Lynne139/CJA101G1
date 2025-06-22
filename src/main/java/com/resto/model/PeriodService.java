package com.resto.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PeriodService {
	
	@Autowired
    private PeriodRepository periodRepository;

    public List<PeriodVO> getPeriodsByRestoId(Integer restoId) {
        return periodRepository.findByRestoVO_RestoId(restoId);
    }
    
    
    
    
}
