package com.resto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resto.entity.PeriodVO;
import com.resto.entity.RestoVO;
import com.resto.model.PeriodService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class TimeslotController {
	
	@Autowired
	RestoService restoService;
	
	@Autowired
	PeriodService periodService;
	
	@Autowired
	TimeslotService timeslotService;
	
	
	// ===== restoInfo.html ====================================================== //
	
	// ===== 新增區段 =====
	
	



}
