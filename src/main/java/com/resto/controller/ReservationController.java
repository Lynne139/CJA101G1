package com.resto.controller;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
	ReservationService reservationService;

	// ===== restoOrder.html ====================================================== //

	// 取得該餐廳每日每時段剩餘名額
	@GetMapping("/api/reservation/remaining")
	@ResponseBody
	public Map<String,Integer> remaining(
		    @RequestParam Integer restoId,
		    @RequestParam Integer timeslotId,
		    @RequestParam
		    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
		    LocalDate date) {

		    int remaining = reservationService.getRemaining(restoId,timeslotId,date);
		    return Map.of("remaining", remaining);
		}
	
	
	
	
	
	

	
}