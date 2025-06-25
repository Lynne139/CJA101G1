package com.resto.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.TimeslotVO;

@Service
public class TimeslotService {
	
	@Autowired
    private TimeslotRepository timeslotRepository;

    public List<TimeslotVO> getTimeslotsByRestoId(Integer restoId) {
        return timeslotRepository.findByRestoVO_RestoId(restoId);
    }
    
    
    
    
    
    
    
    
    


}
