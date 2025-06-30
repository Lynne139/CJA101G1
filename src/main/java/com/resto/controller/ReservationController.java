package com.resto.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.resto.model.ReservationService;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;

@Controller
@RequestMapping("/admin")
public class ReservationController {
	
	@Autowired
	RestoOrderService restoOrderService;
	@Autowired
	RestoService restoService;
	@Autowired
	TimeslotService timeslotService;
	@Autowired
	ReservationService reservationService;

	// ===== restoOrder.html ====================================================== //

	@GetMapping("/api/reservation/full-dates")
	@ResponseBody
	public List<LocalDate> getFullDates(@RequestParam Integer restoId) {
	    return reservationService.getFullBookedDates(restoId);
	}
	
	
	
	
	
	
	

	
}
