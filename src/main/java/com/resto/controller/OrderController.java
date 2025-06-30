package com.resto.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.ResponseBody;
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
	
	
	// ===== 新增 =====
	//取得add modal
	@GetMapping("/resto_order/add")
	public String showAddModal(Model model) {
		RestoOrderVO restoOrder = new RestoOrderVO();
	    restoOrder.setOrderSource(RestoOrderSource.ADMIN); // 預設為 ADMIN
	    
	    List<RestoVO> restoVOList = restoService.getAll();   // 只抓啟用的餐廳
	    List<TimeslotVO> timeslotVOList = timeslotService.getAllEnabled();
	    
	    model.addAttribute("restoOrder", restoOrder);
	    model.addAttribute("restoVOList", restoVOList);
	    model.addAttribute("timeslotVOList", timeslotVOList);
	    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
	    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));

	    return "admin/fragments/resto/modals/order_resto_add";
	}
	
	//寫入新增內容到資料庫
	@PostMapping("/resto_order/insert")
	public String insertRestoOrder(
			@Validated({Default.class, ValidationGroups.Ordered.class}) @ModelAttribute("restoOrder") RestoOrderVO order,
	        BindingResult result,
	        RedirectAttributes redirectAttributes,
	        Model model
	) {
		
	    // 錯誤 flag（初始 false）
	    boolean hasAnyError = false;
	    
	    if (order.getRestoVO() == null || order.getRestoVO().getRestoId() == null) {
	        result.rejectValue("restoVO.restoId", null, "請選擇餐廳");
	    }
	    if (order.getTimeslotVO() == null || order.getTimeslotVO().getTimeslotId() == null) {
	        result.rejectValue("timeslotVO.timeslotId", null, "請選擇時段");
	    }
	    
	    
	    // 假如有錯誤導致管理員可以選到非屬於餐廳的時段，要擋下
	    if (!result.hasErrors()
	    	    && order.getRestoVO() != null
	    	    && order.getTimeslotVO() != null
	    	    && order.getTimeslotVO().getRestoVO() != null
	    	    && !order.getTimeslotVO().getRestoVO().getRestoId().equals(order.getRestoVO().getRestoId())) {

	    	    result.rejectValue("timeslotVO.timeslotId", null, "此時段不屬於所選餐廳");
	    	}
	    
	   
	       // 管理員系統
	    
	    // 若欄位驗證有錯，回填 modal
	    if (result.hasErrors() || hasAnyError) {
	    	// 把下拉清單再次塞回去
	        model.addAttribute("restoVOList", restoService.getAll());
	        model.addAttribute("timeslotVOList", timeslotService.getTimeslotsByRestoId(order.getRestoVO().getRestoId()));
		    model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
		    model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
	        model.addAttribute("order", order);
	        System.out.println("xxx");
	        
	        
	     // 把所有 field / object error 都 dump 出來
	        result.getAllErrors().forEach(err -> {
	            System.out.printf(
	                "[%s] field=%s | code=%s | msg=%s%n",
	                err.getClass().getSimpleName(),          // FieldError / ObjectError
	                (err instanceof org.springframework.validation.FieldError fe) ? fe.getField() : "-",
	                err.getCode(),                           // NotNull、Pattern…
	                err.getDefaultMessage()                  // 你 annotation 寫的 message
	            );
	        });
	        
	        
	        
	        
	        return "admin/fragments/resto/modals/order_resto_add";
	    }
        System.out.println("000");

	    // 寫入資料庫
		restoOrderService.insert(order);


	    return "redirect:/admin/resto_order";
	}

	


//	// ===== 修改 =====
//	//取得edit modal
//	@GetMapping("/resto_info/edit")
//	public String showEditModal(@RequestParam("restoId") Integer id, Model model) {
//	    RestoVO resto = restoService.getById(id);
//
//	    model.addAttribute("resto", resto);
//	    return "admin/fragments/resto/modals/resto_edit :: editModalContent";
//	}
//	
//	//寫入新增內容到資料庫
//	@PostMapping("/resto_info/update")
//	public String updateResto(
//	        @Valid @ModelAttribute("resto") RestoVO resto,
//	        BindingResult result,
//	        @RequestParam(value = "uploadImg", required = false) MultipartFile imageFile,
//	        @RequestParam(value = "clearImgFlag", required = false) String clearImgFlag,
//	        RedirectAttributes redirectAttributes,
//	        Model model
//	) {
//		
//	    // 錯誤 flag（初始 false）
//	    boolean hasAnyError = false;
//
//	    // 處理圖片格式與大小
//	    if (imageFile != null && !imageFile.isEmpty()) {
//	        String contentType = imageFile.getContentType();
//	        long maxSize = 16 * 1024 * 1024;
//
//	        if (!isValidImageType(contentType)) {
//	            model.addAttribute("imageError", "只接受 PNG / JPEG / GIF 格式圖片");
//	            hasAnyError = true;
//	        } else if (imageFile.getSize() > maxSize) {
//	            model.addAttribute("imageError", "圖片大小不得超過 16MB");
//	            hasAnyError = true;
//	        } else {
//	            try {
//	                resto.setRestoImg(imageFile.getBytes());
//	            } catch (IOException e) {
//	                model.addAttribute("imageError", "圖片處理失敗");
//	                hasAnyError = true;
//	            }
//	        }
//	    }
//
//	    // 驗證名稱重複
//	    if (restoService.existsDuplicateName(resto)) {
//	        result.rejectValue("restoName", null, "該餐廳名稱已存在，請重新輸入！");
//	    }
//
//	    // 若欄位驗證有錯，或圖片錯誤，回填 modal
//	    if (result.hasErrors() || hasAnyError) {
//	    	// 把資料庫圖片補回去(避免input有新選其他圖，但表單驗證被擋時，回填的model記成input失敗的內容導致preview錯亂)
//	        byte[] originalImg = restoService.getById(resto.getRestoId()).getRestoImg();
//	        resto.setRestoImg(originalImg);
//
//	        model.addAttribute("resto", resto);
//	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
//	    }
//	    
//	    //送出時以version判斷是否期間有人做更動避免覆蓋更新
//	    try {
//		    restoService.saveWithImage(resto, imageFile, clearImgFlag);
//	    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
//	        model.addAttribute("errorMsg", e.getMessage());
//	        model.addAttribute("resto", resto); // 回填原輸入
//	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
//	    }
//	    return "redirect:/admin/resto_info";
//	}


	

	@GetMapping("/api/reservation/available-dates")
	@ResponseBody
	public List<LocalDate> getAvailableDates(@RequestParam Integer restoId) {
	    return reservationService.getAvailableDates(restoId);
	}
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	

	
}
