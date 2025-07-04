package com.resto.controller;

import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
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
    @PostMapping("/resto_timeslot/timeslot/delete")
    public String softDelete(@RequestParam Integer timeslotId, 
					         @RequestParam Integer restoId, 
					    	 RedirectAttributes redirectAttributes,
					    	 Model model
	) {
        timeslotService.softDelete(timeslotId);
        
        redirectAttributes.addAttribute("restoId", restoId);
        return "redirect:/admin/resto_timeslot";
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
            result.rejectValue("timeslotName", null, "該時段已存在，請重新輸入！");
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
	        result.rejectValue("timeslotName", null, "該時段已存在，請重新輸入！");
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
	}
	
	//拖動改periodId
	@PostMapping("/resto_timeslot/timeslot/transfer")
	@ResponseBody
	public Map<String, Object>
	transferTimeslot(@RequestParam Integer timeslotId,
	                 @RequestParam Integer newPeriodId) {

	    TimeslotVO ts = timeslotService.getById(timeslotId);
	    PeriodVO newPeriod = periodService.getById(newPeriodId);

	    // 檢查同餐廳才能搬
	    if (!Objects.equals(ts.getRestoVO().getRestoId(),
	                        newPeriod.getRestoVO().getRestoId())) {
	        return Map.of("msg", "error",
	                      "message", "不支援跨餐廳移動，若需新增請直接於該餐廳頁面添加");
	    }

	    ts.setPeriodVO(newPeriod);   // 更新關聯 (JPA 自動改 period_id)
	    timeslotService.update(ts);

	    return Map.of("msg", "transfer success");
	}
	
	
		



}
