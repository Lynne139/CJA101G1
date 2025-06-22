package com.resto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.resto.model.TimeslotService;

@Controller
@RequestMapping("/admin")
public class TimeslotController {
	
	@Autowired
	TimeslotService timeslotService;

}
