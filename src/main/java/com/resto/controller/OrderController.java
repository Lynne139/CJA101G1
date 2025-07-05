package com.resto.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.ReservationService;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderStatus;
import com.resto.utils.ValidationGroups;
import com.resto.utils.exceptions.OverbookingException;

import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

@Controller
@RequestMapping("/admin")
public class OrderController {
	
	@Autowired
	RestoOrderService restoOrderService;
	@Autowired
	RestoService restoService;
	@Autowired
	TimeslotService timeslotService;
	@Autowired
	ReservationService reservationService;

	// ===== restoOrder.html ====================================================== //

	// ===== 細項檢視 =====
	@GetMapping("/resto_order/view")
	public String showViewModal(@RequestParam("restoOrderId") Integer id, Model model) {

	    RestoOrderVO restoOrder = restoOrderService.getById(id);
	    model.addAttribute("restoOrder", restoOrder);
	    return "admin/fragments/resto/modals/order_resto_view :: viewModalContent";
	}
	
	
	// ===== 刪除 =====
	@PostMapping("/resto_order/delete")
	public String deleteRestoOrder(@RequestParam("restoOrderId") Integer restoOrderId,
							   RedirectAttributes redirectAttributes
	) {
		
		restoOrderService.deleteById(restoOrderId);
	    return "redirect:/admin/resto_order";
	}
		
	
	// ===== 新增 =====
	//取得add modal
	@GetMapping("/resto_order/add")
	public String showAddModal(Model model) {
		RestoOrderVO restoOrder = new RestoOrderVO();
	    restoOrder.setOrderSource(RestoOrderSource.ADMIN); // 預設為ADMIN
	    
	    List<RestoVO> restoVOList = restoService.getAll();   // 只抓啟用的餐廳
	    List<TimeslotVO> timeslotVOList = timeslotService.getAllEnabled();
	    
	    model.addAttribute("restoOrder", restoOrder);
	    model.addAttribute("restoVOList", restoVOList);
	    model.addAttribute("timeslotVOList", timeslotVOList);
	    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
	    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
	    // 抓當日日期，避免過往日期可預約
	    model.addAttribute("today", LocalDate.now());

	    return "admin/fragments/resto/modals/order_resto_add";
	}
	
	//寫入新增內容到資料庫
	@PostMapping("/resto_order/insert")
	public String insertRestoOrder(
			@Validated({Default.class, ValidationGroups.Ordered.class}) 
			@ModelAttribute("restoOrder") RestoOrderVO restoOrder,
	        BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {


		// 錯誤 flag（初始 false）
	    boolean hasAnyError = false;
		
		// valid+bindingresult只驗證基本型別與嵌套物件，關聯的捕捉要自己寫
	    if (restoOrder.getRestoVO() == null || restoOrder.getRestoVO().getRestoId() == null) {
	        result.rejectValue("restoVO.restoId", null, "請選擇餐廳");
	        hasAnyError = true;
	    }
	    if (restoOrder.getTimeslotVO() == null || restoOrder.getTimeslotVO().getTimeslotId() == null) {
	        result.rejectValue("timeslotVO.timeslotId", null, "請選擇時段");
	        hasAnyError = true;
	    }
  
    	// 自行補齊 timeslotVO（含 restoVO）
	    if (!result.hasErrors()
	            && restoOrder.getTimeslotVO() != null
	            && restoOrder.getTimeslotVO().getTimeslotId() != null) {
	    	restoOrder.setTimeslotVO(timeslotService.getById(restoOrder.getTimeslotVO().getTimeslotId()));
	    }

	    // 防呆，檢查是否選到別間餐廳的時段
	    if (!result.hasErrors()
	            && restoOrder.getRestoVO() != null
	            && restoOrder.getTimeslotVO() != null
	            && restoOrder.getTimeslotVO().getRestoVO() != null
	            && !restoOrder.getTimeslotVO().getRestoVO().getRestoId().equals(restoOrder.getRestoVO().getRestoId())) {

	        result.rejectValue("timeslotVO.timeslotId",
	                "timeslot.mismatch",
	                "此時段不屬於所選餐廳");
	        hasAnyError = true;
	    }

	    // 防呆，檢查是否選到已過的時段
	    if (!result.hasErrors()
	            && restoOrder.getRegiDate() != null
	            && restoOrder.getTimeslotVO() != null
	            && restoOrder.getTimeslotVO().getTimeslotName() != null) {
	        try {
	            LocalDateTime slotDateTime = LocalDateTime.of(
	            		restoOrder.getRegiDate(),
	                    LocalTime.parse(restoOrder.getTimeslotVO().getTimeslotName()));

	            if (!slotDateTime.isAfter(LocalDateTime.now())) {
	                result.rejectValue("timeslotVO.timeslotId",
	                        "timeslot.past",
	                        "此時段已過，請選擇未來時間");
	    	        hasAnyError = true;
	            }
	        } catch (DateTimeParseException e) {
	            result.rejectValue("timeslotVO.timeslotId",
	                    null,
	                    "時段格式錯誤，請聯繫管理員");
		        hasAnyError = true;
	        }
	    }

	    
	    // 名額驗證
	    if (!result.hasErrors()) {   // 其他欄位都OK時才檢查
	        int remaining = reservationService.getRemaining(
	        		restoOrder.getRestoVO().getRestoId(),
	        		restoOrder.getTimeslotVO().getTimeslotId(),
	        		restoOrder.getRegiDate());

	        if (restoOrder.getRegiSeats() > remaining) {
	            result.rejectValue(    // 綁定在regiSeats欄位
	                    "regiSeats",
	                    "seats.exceed",      // errorCode(messages.properties配文案)
	                    new Object[]{remaining},          // 參數
	                    "剩餘 " + remaining + " 位，名額不足"   // 預設訊息
	            );
	        }
	    }
 
	    // 防呆，擋掉過往日期
	    if (restoOrder.getRegiDate() != null && restoOrder.getRegiDate().isBefore(LocalDate.now())) {
	        result.rejectValue("regiDate", "invalid.past.date", "不能預約過去的日期");
	    }	    
	    
	   
	    // 管理員系統
	    // 若欄位驗證有錯，回填 modal
	    if (result.hasErrors()  || hasAnyError) {
	    	// 把下拉清單再次塞回去
	        model.addAttribute("restoVOList", restoService.getAll());
	        model.addAttribute("timeslotVOList", timeslotService.getAllEnabled());
		    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
		    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
		    model.addAttribute("today", LocalDate.now());     // 加回 today，避免 JS 抓不到 min 值
	        model.addAttribute("restoOrder", restoOrder);


	       // 除錯用:把所有 field/object error 都列出
//	        result.getAllErrors().forEach(err -> {
//	            System.out.printf(
//	                "[%s] field=%s | code=%s | msg=%s%n",
//	                err.getClass().getSimpleName(),          // FieldError / ObjectError
//	                (err instanceof org.springframework.validation.FieldError fe) ? fe.getField() : "-",
//	                err.getCode(),                           // NotNull、Pattern等
//	                err.getDefaultMessage()                  // annotation 寫的 message
//	            );
//	        });
//	        
   
	        return "admin/fragments/resto/modals/order_resto_add";
	    }

	    // 寫入資料庫
		restoOrderService.insert(restoOrder);
	    return "redirect:/admin/resto_order";
	}

	


	// ===== 修改 =====
	//取得edit modal
	@GetMapping("/resto_order/edit")
	public String showEditModal(@RequestParam("restoOrderId") Integer id, Model model) {

	    RestoOrderVO restoOrder = restoOrderService.getById(id);
	    
	    if (restoOrder == null) {
	        model.addAttribute("errorMsg", "該筆訂單不存在或已被刪除");
	        return "admin/fragments/common/error_modal";
	    }
	    
	    
	    // 顯示人預約人數判斷
	    // 把舊預約人數值拷貝出來（純 int，永遠不變）
	    int originalSeats = restoOrder.getRegiSeats(); 
	    int remainingSeats = reservationService.getRemaining(
	    		restoOrder.getRestoVO().getRestoId(),
	    		restoOrder.getTimeslotVO().getTimeslotId(),
	    		restoOrder.getRegiDate());      
	    
	    
	    List<RestoVO> restoVOList = restoService.getAll();   // 只抓啟用的餐廳
	    List<TimeslotVO> timeslotVOList = timeslotService.getAllEnabled();
	    
	    model.addAttribute("restoOrder", restoOrder);
	    model.addAttribute("restoVOList", restoVOList);
	    model.addAttribute("timeslotVOList", timeslotVOList);
	    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
	    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
	    // 抓當日日期，避免過往日期可預約
//	    model.addAttribute("today", LocalDate.now());
	    model.addAttribute("originalSeats", originalSeats);
	    model.addAttribute("remainingSeats", remainingSeats);

	    
	    return "admin/fragments/resto/modals/order_resto_edit";
	}
	
	//寫入新增內容到資料庫
	@PostMapping("/resto_order/update")
	public String updateRestoOrder(
	        @Valid @ModelAttribute("restoOrder") RestoOrderVO restoOrder,
	        BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
		RestoOrderVO original = restoOrderService.getById(restoOrder.getRestoOrderId());
	    if (original == null) {
	    	result.reject("notfound", "資料不存在或已刪除");
	        return "admin/fragments/common/error_modal";
	    }
	    
	    
	    // 顯示人預約人數判斷
	    // 把舊預約人數值拷貝出來（純 int，永遠不變）
	    int originalSeats = original.getRegiSeats(); 
	    int remainingSeats = reservationService.getRemaining(
	    		restoOrder.getRestoVO().getRestoId(),
	    		restoOrder.getTimeslotVO().getTimeslotId(),
	    		restoOrder.getRegiDate()); 


	    // 錯誤 flag（初始 false）
	    boolean hasAnyError = false;
		
		// valid+bindingresult只驗證基本型別與嵌套物件，關聯的捕捉要自己寫
	    if (restoOrder.getRestoVO() == null || restoOrder.getRestoVO().getRestoId() == null) {
	        result.rejectValue("restoVO.restoId", null, "請選擇餐廳");
	        hasAnyError = true;
	    }
	    if (restoOrder.getTimeslotVO() == null || restoOrder.getTimeslotVO().getTimeslotId() == null) {
	        result.rejectValue("timeslotVO.timeslotId", null, "請選擇時段");
	        hasAnyError = true;
	    }
  
    	// 自行補齊 timeslotVO（含 restoVO）
	    if (!result.hasErrors()
	            && restoOrder.getTimeslotVO() != null
	            && restoOrder.getTimeslotVO().getTimeslotId() != null) {
	    	restoOrder.setTimeslotVO(timeslotService.getById(restoOrder.getTimeslotVO().getTimeslotId()));
	    }

	    // 防呆，檢查是否選到別間餐廳的時段
	    if (!result.hasErrors()
	            && restoOrder.getRestoVO() != null
	            && restoOrder.getTimeslotVO() != null
	            && restoOrder.getTimeslotVO().getRestoVO() != null
	            && !restoOrder.getTimeslotVO().getRestoVO().getRestoId().equals(restoOrder.getRestoVO().getRestoId())) {

	        result.rejectValue("timeslotVO.timeslotId",
	                "timeslot.mismatch",
	                "此時段不屬於所選餐廳");
	        hasAnyError = true;
	    }
	    

	    
	    // 管理員系統
	    // 若欄位驗證有錯，回填 modal
	    if (result.hasErrors()  || hasAnyError) {
	    	// 把下拉清單再次塞回去
	        model.addAttribute("restoVOList", restoService.getAll());
	        model.addAttribute("timeslotVOList", timeslotService.getAllEnabled());
		    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
		    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
//		    model.addAttribute("today", LocalDate.now());     // 加回 today，避免 JS 抓不到 min 值
	        model.addAttribute("restoOrder", restoOrder);
		    model.addAttribute("originalSeats", originalSeats);
		    model.addAttribute("remainingSeats", remainingSeats);




	        return "admin/fragments/resto/modals/order_resto_edit";
	    }
	    

	    
	    try {
	    	// 寫入資料庫
	        restoOrderService.update(restoOrder);
	    } catch (OverbookingException e) {
	        result.rejectValue("regiSeats","overbooked", e.getMessage());
	        
	        // 回填
	        model.addAttribute("restoVOList", restoService.getAll());
	        model.addAttribute("timeslotVOList", timeslotService.getAllEnabled());
		    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
		    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
	        model.addAttribute("restoOrder", restoOrder);
		    model.addAttribute("originalSeats", originalSeats);
		    model.addAttribute("remainingSeats", remainingSeats);



	        
	        return "admin/fragments/resto/modals/order_resto_edit";
	    }
	    
	    return "redirect:/admin/resto_order";
	}

	
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	

	
}