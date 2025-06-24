package com.resto.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.resto.model.TimeslotService;

@Controller
@RequestMapping("/admin")
public class TimeslotController {
	
	@Autowired
	TimeslotService timeslotService;
	
	
	// ===== restoInfo.html ====================================================== //
	
	// ===== 編輯所屬區段 =====
	


}
