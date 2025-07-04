package com.shopOrdDet.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;
import java.util.stream.Collectors;

import com.member.model.MemberService;
import com.prod.model.*;
import com.shopOrd.model.ShopOrdService;
import com.shopOrd.model.ShopOrdVO;
import com.shopOrdDet.model.*;
import com.prodCate.model.*;

@Controller
@Validated
@RequestMapping("/admin/shopOrdDet")
public class ShopOrdDetController {

	@Autowired
	ShopOrdDetService shopOrdDetSvc;

	@Autowired
	ProdService prodSvc;
	
	@Autowired
	ShopOrdService shopOrdSvc;
	
	@Autowired
	MemberService memberSvc;
	
	@Autowired
	ProdCateService prodCateSvc;
	
	@GetMapping("listAllShopOrdDet")
	public String listAllShopOrdDet(Model model) {
	    List<ShopOrdDetVO> list = shopOrdDetSvc.getAll();
	    model.addAttribute("shopOrdDetListData", list);
	    return "admin/fragments/shop/shopOrdDet/listAllShopOrdDet";
	}
	
	@GetMapping("listAllShopOrdDetByOrderId")
	public String listAllShopOrdDetByOrderId(@RequestParam("prodOrdId") Integer prodOrdId, Model model) {
	    List<ShopOrdDetVO> list = shopOrdDetSvc.getShopOrdDetByOrderId(prodOrdId);
	    model.addAttribute("shopOrdDetListData", list);
	    model.addAttribute("prodOrdId", prodOrdId);
	    return "admin/fragments/shop/shopOrdDet/listAllShopOrdDet";
	}

	@ModelAttribute("shopOrdDetListData")
	protected List<ShopOrdDetVO> referenceListData(Model model) {
    	List<ShopOrdDetVO> list = shopOrdDetSvc.getAll();
		return list;
	}
    
	@ModelAttribute("prodListData")
	protected List<ProdVO> referenceListData_Prod(Model model) {
		List<ProdVO> list = prodSvc.getAll();
		return list;
	}
	
	@ModelAttribute("shopOrdListData")
	protected List<ShopOrdVO> referenceListData_ShopOrd(Model model) {
		List<ShopOrdVO> list = shopOrdSvc.getAll();
		return list;
	}

	@GetMapping("addShopOrdDet")
	public String addShopOrdDet(ModelMap model) {
		ShopOrdDetVO shopOrdDetVO = new ShopOrdDetVO();
		// 初始化複合主鍵
		ShopOrdDetIdVO id = new ShopOrdDetIdVO();
		shopOrdDetVO.setPpid(id);
		
		model.addAttribute("shopOrdDetVO", shopOrdDetVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
		return "admin/fragments/shop/shopOrdDet/addShopOrdDet";
	}

	@PostMapping("addShopOrdDet")
	public String insert(@Valid ShopOrdDetVO shopOrdDetVO, BindingResult result, ModelMap model, HttpServletRequest request) {

	    if (result.hasErrors()) {
	        // 檢查是否是 AJAX 請求
	        String xRequestedWith = request.getHeader("X-Requested-With");
	        if ("XMLHttpRequest".equals(xRequestedWith)) {
	            // AJAX 請求，返回錯誤頁面內容
	            model.addAttribute("shopOrdDetVO", shopOrdDetVO);
	            model.addAttribute("prodListData", prodSvc.getAll());
	            model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
	            return "admin/fragments/shop/shopOrdDet/addShopOrdDet";
	        } else {
	            // 傳統表單提交，重定向到 select_page
	            return "redirect:/admin/shopOrdDet/select_page?errorMessage=請檢查輸入資料，所有欄位都必須填寫";
	        }
	    }

	    // 設置複合主鍵
	    if (shopOrdDetVO.getPpid() == null) {
	        ShopOrdDetIdVO id = new ShopOrdDetIdVO();
	        if (shopOrdDetVO.getShopOrdVO() != null) {
	            id.setProdOrdId(shopOrdDetVO.getShopOrdVO().getProdOrdId());
	        }
	        if (shopOrdDetVO.getProdVO() != null) {
	            id.setProductId(shopOrdDetVO.getProdVO().getProductId());
	        }
	        shopOrdDetVO.setPpid(id);
	    }
	    
	    shopOrdDetSvc.addOrUpdateShopOrdDet(shopOrdDetVO);

	    return "redirect:/admin/shopOrdDet/select_page?success=true";
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("prodOrdId") Integer prodOrdId, 
	                               @RequestParam("productId") Integer productId, 
	                               ModelMap model) {
		ShopOrdDetVO shopOrdDetVO = shopOrdDetSvc.getOneShopOrdDet(prodOrdId, productId);
		
		if (shopOrdDetVO == null) {
			model.addAttribute("errorMessage", "找不到指定的訂單明細");
			return "redirect:/admin/shopOrdDet/select_page";
		}

		model.addAttribute("shopOrdDetVO", shopOrdDetVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
		return "admin/fragments/shop/shopOrdDet/update_shopOrdDet_input";
	}

	@PostMapping("update")
	public String update(@Valid ShopOrdDetVO shopOrdDetVO, BindingResult result, ModelMap model, HttpServletRequest request) {

		if (result.hasErrors()) {
			// 檢查是否是 AJAX 請求
			String xRequestedWith = request.getHeader("X-Requested-With");
			if ("XMLHttpRequest".equals(xRequestedWith)) {
				// AJAX 請求，返回錯誤頁面內容
				model.addAttribute("shopOrdDetVO", shopOrdDetVO);
				model.addAttribute("prodListData", prodSvc.getAll());
				model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
				return "admin/fragments/shop/shopOrdDet/update_shopOrdDet_input";
			} else {
				// 傳統表單提交，返回錯誤頁面
				model.addAttribute("prodListData", prodSvc.getAll());
				model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
				return "admin/fragments/shop/shopOrdDet/update_shopOrdDet_input";
			}
		}
		
		// 確保複合主鍵被正確設置
		if (shopOrdDetVO.getPpid() == null) {
		    ShopOrdDetIdVO id = new ShopOrdDetIdVO();
		    shopOrdDetVO.setPpid(id);
		}
		
		// 如果複合主鍵的 ID 為 null，從隱藏欄位中獲取
		if (shopOrdDetVO.getPpid().getProdOrdId() == null || shopOrdDetVO.getPpid().getProductId() == null) {
		    // 從請求參數中獲取
		    String prodOrdIdStr = request.getParameter("ppid.prodOrdId");
		    String productIdStr = request.getParameter("ppid.productId");
		    
		    if (prodOrdIdStr != null && productIdStr != null) {
		        shopOrdDetVO.getPpid().setProdOrdId(Integer.valueOf(prodOrdIdStr));
		        shopOrdDetVO.getPpid().setProductId(Integer.valueOf(productIdStr));
		    }
		}
		
		shopOrdDetSvc.addOrUpdateShopOrdDet(shopOrdDetVO);

		return "redirect:/admin/shopOrdDet/select_page?updateSuccess=true";
	}

	@PostMapping("delete")
	public String delete(@RequestParam("prodOrdId") Integer prodOrdId, 
	                    @RequestParam("productId") Integer productId, 
	                    ModelMap model) {
		shopOrdDetSvc.deleteShopOrdDet(prodOrdId, productId);
		return "redirect:/admin/shopOrdDet/select_page?success=true&message=刪除成功";
	}

	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam(value = "prodOrdId", required = false) Integer prodOrdId,
	                                @RequestParam(value = "productId", required = false) Integer productId,
	                                Model model) {
	    if (prodOrdId == null || productId == null) {
	        return "redirect:/admin/shopOrdDet/select_page?errorMessage=訂單編號和商品編號請勿空白";
	    }

	    ShopOrdDetVO shopOrdDetVO = shopOrdDetSvc.getOneShopOrdDet(prodOrdId, productId);
	    if (shopOrdDetVO == null) {
	        return "redirect:/admin/shopOrdDet/select_page?errorMessage=查無資料";
	    } else {
	        model.addAttribute("shopOrdDetVO", shopOrdDetVO);
	        return "redirect:/admin/shopOrdDet/select_page?prodOrdId=" + prodOrdId + "&productId=" + productId;
	    }
	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ShopOrdDetVO shopOrdDetVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(shopOrdDetVO, "shopOrdDetVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public String handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations ) {
			strBuilder.append(violation.getMessage()).append(" ");
		}
		String message = strBuilder.toString();
		return "redirect:/admin/shopOrdDet/select_page?errorMessage=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
	}

	// 唯一訂單編號清單
	@ModelAttribute("uniqueProdOrdIdList")
	public Set<Integer> getUniqueProdOrdIdList() {
		return shopOrdDetSvc.getAll().stream()
				.map(vo -> vo.getShopOrdVO().getProdOrdId())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	// 唯一商品編號清單
	@ModelAttribute("uniqueProductIdList")
	public Set<Integer> getUniqueProductIdList() {
		return shopOrdDetSvc.getAll().stream()
				.map(vo -> vo.getProdVO().getProductId())
				.collect(Collectors.toCollection(LinkedHashSet::new));
	}

	

}
