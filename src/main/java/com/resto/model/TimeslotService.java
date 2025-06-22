package com.resto.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TimeslotService {
	
	@Autowired
    private TimeslotRepository timeslotRepository;

    public List<TimeslotVO> getTimeslotsByRestoId(Integer restoId) {
        return timeslotRepository.findByRestoVO_RestoId(restoId);
    }

}
