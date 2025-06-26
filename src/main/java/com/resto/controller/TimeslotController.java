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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resto.entity.TimeslotVO;
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
	
	
	// ===== restoTimeslot.html ====================================================== //
	
	// === 軟刪除 ===
    @GetMapping("/resto_timeslot/timeslot/delete")
    public String softDelete(@RequestParam Integer timeslotId, 
					         @RequestParam Integer restoId, 
					    	 RedirectAttributes redirectAttributes,
					    	 Model model
	) {
        timeslotService.softDelete(timeslotId);
        
        redirectAttributes.addAttribute("restoId", restoId);
        return "redirect:/admin/resto_timeslot";
        
//        model.addAttribute("periodList", periodService.getPeriodsByRestoId(restoId));
//        model.addAttribute("timeslotList", timeslotService.getTimeslotsByRestoId(restoId));
//        return "forward:/admin/resto_timeslot?restoId=" + restoId;
    }
    
	
	// ===== 新增區段 =====
	//取得add modal	
	@GetMapping("/resto_timeslot/timeslot/add")
	public String showAddModal(@RequestParam Integer periodId, Model model) {

        TimeslotVO timeslot = new TimeslotVO();
        
        timeslot.setPeriodVO(periodService.getById(periodId));
        timeslot.setRestoVO(timeslot.getPeriodVO().getRestoVO());

        model.addAttribute("timeslot", timeslot);
        return "admin/fragments/resto/modals/timeslot_add";
    }

	//寫入新增內容到資料庫
	@PostMapping("/resto_timeslot/timeslot/insert")
	public String insertTimeslot(
			@Valid @ModelAttribute("timeslot") TimeslotVO timeslot, 
			BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
		// 錯誤 flag（初始 false）
		boolean hasAnyError = false;
	
		// 驗證名稱重複
		if (timeslotService.existsDuplicateName(timeslot)) {
            result.rejectValue("timeslotName", null, "該時段名稱已存在，請重新輸入！");
            hasAnyError = true;
        }

        if (result.hasErrors() || hasAnyError) {
            model.addAttribute("timeslot", timeslot);
            return "admin/fragments/resto/modals/timeslot_add"; // 回傳含錯誤的modal
        }

        // 寫入資料庫
        timeslotService.insert(timeslot);
        
	    redirectAttributes.addAttribute("restoId", timeslot.getRestoVO().getRestoId());
	    return "redirect:/admin/resto_timeslot";
 
//        Integer restoId = timeslot.getRestoVO().getRestoId();
//        model.addAttribute("periodList", periodService.getPeriodsByRestoId(restoId));
//        model.addAttribute("timeslotList", timeslotService.getTimeslotsByRestoId(restoId));
//        return "forward:/admin/resto_timeslot?restoId=" + restoId;
	}
	
	
	// ===== 修改 =====
	//取得edit modal
	@GetMapping("/resto_timeslot/timeslot/edit")
	public String showEditModal(@RequestParam Integer timeslotId,
								Model model
	) {
		
		TimeslotVO timeslot = timeslotService.getById(timeslotId);
		
	    
	    model.addAttribute("timeslot", timeslot);
        return "admin/fragments/resto/modals/timeslot_edit";
	}
	
	//寫入編輯內容到資料庫
	@PostMapping("/resto_timeslot/timeslot/update")
	public String updateTimeslot(
	        @Valid @ModelAttribute("timeslot") TimeslotVO timeslot,
	        BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
		Integer restoId = timeslot.getRestoVO().getRestoId();
		
	    // 錯誤 flag（初始 false）
	    boolean hasAnyError = false;

	    // 驗證名稱重複
	    if (timeslotService.existsDuplicateName(timeslot)) {
	        result.rejectValue("timeslotName", null, "該時段名稱已存在，請重新輸入！");
            hasAnyError = true;
	    }

	    // 若欄位驗證有錯，回填 modal
	    if (result.hasErrors() || hasAnyError) {
	        model.addAttribute("timeslot", timeslot);
	        return "admin/fragments/resto/modals/timeslot_edit";
	    }
	    
	   // 寫入資料庫
	    timeslotService.update(timeslot);

	    redirectAttributes.addAttribute("restoId", restoId);
	    return "redirect:/admin/resto_timeslot";
	    
	    
//        model.addAttribute("periodList", periodService.getPeriodsByRestoId(restoId));
//        model.addAttribute("timeslotList", timeslotService.getTimeslotsByRestoId(restoId));
//        return "forward:/admin/resto_timeslot?restoId=" + restoId;
	}
		



}
