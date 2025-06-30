package com.resto.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.resto.entity.RestoOrderVO;
import com.resto.model.RestoOrderService;

@Controller
@RequestMapping("/admin")
public class OrderController {
	
	@Autowired
	RestoOrderService restoOrderService;

	// ===== restoOrder.html ====================================================== //

	// ===== 細項檢視 =====
	@GetMapping("/resto_order/view")
	public String showViewModal(@RequestParam("restoOrderId") Integer id, Model model) {

	    RestoOrderVO restoOrder = restoOrderService.getById(id);
	    model.addAttribute("restoOrder", restoOrder);
	    return "admin/fragments/resto/modals/order_resto_view :: viewModalContent";
	}
	
	
//	// ===== 新增 =====
//	//取得add modal
//	@GetMapping("/resto_info/add")
//	public String showAddModal(Model model) {
//	    model.addAttribute("resto", new RestoVO()); // 傳入一個空的RestoVO
//	    return "admin/fragments/resto/modals/resto_add :: addModalContent";
//	}
//	
//	//寫入新增內容到資料庫
//	@PostMapping("/resto_info/insert")
//	public String insertResto(
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
//	    // 檢查圖片格式與大小
//	    if (imageFile != null && !imageFile.isEmpty()) {
//	        String contentType = imageFile.getContentType();
//	        long maxSize = 16 * 1024 * 1024; // 16MB
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
//	        hasAnyError = true;
//	    }
//
//	    // 若欄位驗證有錯，或圖片錯誤，回填 modal
//	    if (result.hasErrors() || hasAnyError) {
//	    	// 避免input有新選其他圖，但表單驗證被擋時，回填的model記成input失敗的內容導致preview錯亂
//	    	resto.setRestoImg(null);
//	        model.addAttribute("resto", resto);
//	        return "admin/fragments/resto/modals/resto_add :: addModalContent";
//	    }
//
//	    // 寫入資料庫
//		restoService.saveWithImage(resto, imageFile , clearImgFlag);
//
//	    return "redirect:/admin/resto_info";
//	}
//
//	// 支援格式判斷
//	private boolean isValidImageType(String contentType) {
//	    return contentType != null && (
//	        contentType.equalsIgnoreCase("image/png") ||
//	        contentType.equalsIgnoreCase("image/jpeg") ||
//	        contentType.equalsIgnoreCase("image/gif")
//	    );
//	}
//
//	
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


	

	
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	

	
}
