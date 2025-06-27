package com.resto.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resto.entity.PeriodVO;
import com.resto.model.PeriodService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class PeriodController {
	
	@Autowired
	RestoService restoService;
	
	@Autowired
	PeriodService periodService;
	
	
	// ===== restoInfo.html ====================================================== //
	
	// ===== 刪除 =====
		@GetMapping("/resto_timeslot/period/delete")
		public String deletePeriod(@RequestParam("periodId") Integer periodId,
								   @RequestParam Integer restoId,
								   RedirectAttributes redirectAttributes
		) {
			
			periodService.deleteById(periodId);
		    redirectAttributes.addAttribute("restoId", restoId);
		    return "redirect:/admin/resto_timeslot";
		}
	
		
	// ===== 新增區段 =====
	//取得add modal	
	@GetMapping("/resto_timeslot/period/add")
	public String showAddModal(@RequestParam Integer restoId, Model model) {

		PeriodVO period = new PeriodVO();
	    
	    period.setRestoVO(restoService.getById(restoId));
	    model.addAttribute("period", period);
	    return "admin/fragments/resto/modals/period_add";
	}
	
	//寫入新增內容到資料庫
	@PostMapping("/resto_timeslot/period/insert")
	public String insertPeriod(
			@Valid @ModelAttribute("period") PeriodVO period, 
			BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	    ) {
		
		// 錯誤 flag（初始 false）
	    boolean hasAnyError = false;
		
		// 驗證名稱重複
	    if (periodService.existsDuplicateName(period)) {
	        result.rejectValue("periodName", null, "類別名稱已存在，請重新輸入！");
	        hasAnyError = true;
	    }
		
	    if (result.hasErrors() || hasAnyError) {
	    	model.addAttribute("period", period);
	        return "admin/fragments/resto/modals/period_add"; // 回傳含錯誤的modal
	    }
	    
	    // 寫入資料庫
	    periodService.insert(period);
	    
	    redirectAttributes.addAttribute("restoId", period.getRestoVO().getRestoId());
	    return "redirect:/admin/resto_timeslot";
	}
	
	// ===== 修改 =====
	//取得edit modal
	@GetMapping("/resto_timeslot/period/edit")
	public String showEditModal(@RequestParam Integer periodId,
								@RequestParam Integer restoId,
								Model model
	) {
		
		PeriodVO period = periodService.getById(periodId);
		
	    model.addAttribute("period", period);
	    model.addAttribute("restoId", restoId);
	    return "admin/fragments/resto/modals/period_edit";
	}

	//寫入編輯內容到資料庫
	@PostMapping("/resto_timeslot/period/update")
	public String updatePeriod(
	        @Valid @ModelAttribute("period") PeriodVO period,
	        BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
		Integer restoId = period.getRestoVO().getRestoId();
		
	    // 錯誤 flag（初始 false）
	    boolean hasAnyError = false;

	    // 驗證名稱重複
	    if (periodService.existsDuplicateName(period)) {
	        result.rejectValue("periodName", null, "該類別名稱已存在，請重新輸入！");
            hasAnyError = true;
	    }

	    // 若欄位驗證有錯，回填 modal
	    if (result.hasErrors() || hasAnyError) {
	        model.addAttribute("period", period);
	        return "admin/fragments/resto/modals/period_edit";
	    }
	    
	   // 寫入資料庫
	    periodService.update(period);
	    redirectAttributes.addAttribute("restoId", restoId);
	    return "redirect:/admin/resto_timeslot";
	}

	
	// ===== 排序 =====
	@PostMapping("/resto_timeslot/period/move")
	@ResponseBody
	public Map<String, Object> move(
	        @RequestParam Integer periodId,
	        @RequestParam String dir
	){
	    periodService.move(periodId, dir);
	    return Map.of("msg", "move success");
	}



}
