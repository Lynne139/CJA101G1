package com.prodCart.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.member.model.MemberService;
import com.prod.model.*;
import com.prodCart.model.*;
import com.prodCate.model.*;
import com.prodPhoto.model.ProdPhotoVO;

@Controller
@Validated
@RequestMapping("/prodCart")
public class ProdCartController {

	@Autowired
	ProdCartService prodCartSvc;

	@Autowired
	ProdService prodSvc;
	
	@Autowired
	MemberService memberSvc;
	
	@Autowired
	ProdCateService prodCateSvc;
	
	@GetMapping("/admin/prodCart/select_page")
	public String selectPage(Model model) {
		model.addAttribute("prodCartVO", new ProdCartVO());
		model.addAttribute("prodCartListData", prodCartSvc.getAll());
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("memberListData", memberSvc.getAll());
		model.addAttribute("mainFragment", "admin/fragments/shop/prodCart/select_page :: content");
		return "admin/index_admin";
	}
	
	@GetMapping("listAllProdCart")
	public String listAllProdCart(Model model) {
	    List<ProdCartVO> list = prodCartSvc.getAll();
	    model.addAttribute("prodCartListData", list);
	    return "admin/fragments/shop/prodCart/listAllProdCart";
	}
	
	@GetMapping("listAllProdCartByMemberId")
	public String listAllProdCartByMemberId(@RequestParam("memberId") Integer memberId, Model model) {
	    List<ProdCartVO> list = prodCartSvc.getProdCartByMemberId(memberId);
	    model.addAttribute("prodCartListData", list);
	    model.addAttribute("memberId", memberId);
	    return "admin/fragments/shop/prodCart/listAllProdCart";
	}

	@ModelAttribute("prodCartListData")
	protected List<ProdCartVO> referenceListData(Model model) {
    	List<ProdCartVO> list = prodCartSvc.getAll();
		return list;
	}
    
	@ModelAttribute("prodListData")
	protected List<ProdVO> referenceListData_Prod(Model model) {
		List<ProdVO> list = prodSvc.getAll();
		return list;
	}
	
	@ModelAttribute("memberListData")
	protected List<com.member.model.MemberVO> referenceListData_Member(Model model) {
		List<com.member.model.MemberVO> list = memberSvc.getAll();
		return list;
	}

	@GetMapping("addProdCart")
	public String addProdCart(ModelMap model) {
		ProdCartVO prodCartVO = new ProdCartVO();
		// 初始化複合主鍵
		ProdMemberIdVO id = new ProdMemberIdVO();
		prodCartVO.setPmid(id);
		
		model.addAttribute("prodCartVO", prodCartVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("memberListData", memberSvc.getAll());
		return "admin/fragments/shop/prodCart/addProdCart";
	}

	@PostMapping("insert")
	public String insert(@Valid ProdCartVO prodCartVO, BindingResult result, ModelMap model, HttpServletRequest request) {

	    if (result.hasErrors()) {
	        // 檢查是否是 AJAX 請求
	        String xRequestedWith = request.getHeader("X-Requested-With");
	        if ("XMLHttpRequest".equals(xRequestedWith)) {
	            // AJAX 請求，返回錯誤頁面內容
	            model.addAttribute("prodCartVO", prodCartVO);
	            model.addAttribute("prodListData", prodSvc.getAll());
	            model.addAttribute("memberListData", memberSvc.getAll());
	            return "admin/fragments/shop/prodCart/addProdCart";
	        } else {
	            // 傳統表單提交，重定向到 select_page
	            return "redirect:/admin/prodCart/select_page?errorMessage=請檢查輸入資料，所有欄位都必須填寫";
	        }
	    }

	    // 設置複合主鍵
	    if (prodCartVO.getPmid() == null) {
	        ProdMemberIdVO id = new ProdMemberIdVO();
	        if (prodCartVO.getProdVO() != null) {
	            id.setProductId(prodCartVO.getProdVO().getProductId());
	        }
	        if (prodCartVO.getMemberVO() != null) {
	            id.setMemberId(prodCartVO.getMemberVO().getMemberId());
	        }
	        prodCartVO.setPmid(id);
	    }
	    
	    prodCartSvc.addProdCart(prodCartVO);

	    return "redirect:/admin/prodCart/select_page?success=true";
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("productId") Integer productId, 
	                               @RequestParam("memberId") Integer memberId, 
	                               ModelMap model) {
		ProdCartVO prodCartVO = prodCartSvc.getOneProdCart(productId, memberId);
		
		if (prodCartVO == null) {
			model.addAttribute("errorMessage", "找不到指定的購物車項目");
			return "redirect:/admin/prodCart/select_page";
		}

		model.addAttribute("prodCartVO", prodCartVO);
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("memberListData", memberSvc.getAll());
		return "admin/fragments/shop/prodCart/update_prodCart_input";
	}

	@PostMapping("update")
	public String update(@Valid ProdCartVO prodCartVO, BindingResult result, ModelMap model, HttpServletRequest request) {

		if (result.hasErrors()) {
			// 檢查是否是 AJAX 請求
			String xRequestedWith = request.getHeader("X-Requested-With");
			if ("XMLHttpRequest".equals(xRequestedWith)) {
				// AJAX 請求，返回錯誤頁面內容
				model.addAttribute("prodCartVO", prodCartVO);
				model.addAttribute("prodListData", prodSvc.getAll());
				model.addAttribute("memberListData", memberSvc.getAll());
				return "admin/fragments/shop/prodCart/update_prodCart_input";
			} else {
				// 傳統表單提交，返回錯誤頁面
				model.addAttribute("prodListData", prodSvc.getAll());
				model.addAttribute("memberListData", memberSvc.getAll());
				return "admin/fragments/shop/prodCart/update_prodCart_input";
			}
		}
		
		// 確保複合主鍵被正確設置
		if (prodCartVO.getPmid() == null) {
		    ProdMemberIdVO id = new ProdMemberIdVO();
		    prodCartVO.setPmid(id);
		}
		
		// 如果複合主鍵的 ID 為 null，從隱藏欄位中獲取
		if (prodCartVO.getPmid().getProductId() == null || prodCartVO.getPmid().getMemberId() == null) {
		    // 從請求參數中獲取
		    String productIdStr = request.getParameter("pmid.productId");
		    String memberIdStr = request.getParameter("pmid.memberId");
		    
		    if (productIdStr != null && memberIdStr != null) {
		        prodCartVO.getPmid().setProductId(Integer.valueOf(productIdStr));
		        prodCartVO.getPmid().setMemberId(Integer.valueOf(memberIdStr));
		    }
		}

		prodCartSvc.updateProdCart(prodCartVO);
		
		return "redirect:/admin/prodCart/select_page?updateSuccess=true";
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("productId") Integer productId, 
	                    @RequestParam("memberId") Integer memberId, 
	                    ModelMap model) {
		prodCartSvc.deleteProdCart(Integer.valueOf(productId),Integer.valueOf(memberId));
		
		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		List<ProdCartVO> list = prodCartSvc.getAll();
		model.addAttribute("prodCartListData", list); // for listAllEmp.html 第85行用
		model.addAttribute("success", "- (刪除成功)");
		return "redirect:/admin/prodCart/select_page"; // 刪除完成後轉交listAllEmp.html
	}

	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam(value = "productId", required = false) Integer productId,
	                                @RequestParam(value = "memberId", required = false) Integer memberId,
	                                Model model) {
		if (productId == null || memberId == null) {
			model.addAttribute("errorMessage", "請提供商品ID和會員ID");
			return "redirect:/admin/prodCart/select_page";
		}
		
		ProdCartVO prodCartVO = prodCartSvc.getOneProdCart(productId, memberId);
		
		if (prodCartVO == null) {
			model.addAttribute("errorMessage", "找不到指定的購物車項目");
			return "redirect:/admin/prodCart/select_page";
		}
		
		model.addAttribute("prodCartVO", prodCartVO);
		return "admin/fragments/shop/prodCart/listOneProdCart";
	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ProdCartVO prodCartVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(prodCartVO, "prodCartVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public String handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations) {
			strBuilder.append(violation.getPropertyPath() + " 需要的值為: " + violation.getInvalidValue() + " 但您輸入的值為: " + violation.getMessage());
		}
		// 以下 2 行是為了配合 Spring MVC 的 BindingResult 能讀取到錯誤訊息
		model.addAttribute("errorMessage", strBuilder.toString());
		model.addAttribute("prodCartVO", new ProdCartVO());
		model.addAttribute("prodListData", prodSvc.getAll());
		model.addAttribute("memberListData", memberSvc.getAll());
		return "admin/fragments/shop/prodCart/addProdCart";
	}

	@GetMapping("/api/member-cart")
	@ResponseBody
	public List<ProdCartVO> getMemberCart(HttpSession session) {
		com.member.model.MemberVO member = (com.member.model.MemberVO) session.getAttribute("memberVO");
		if (member == null) return Collections.emptyList();
		return prodCartSvc.getProdCartByMemberId(member.getMemberId());
	}

	@PostMapping("/front/add")
	public String addProdCartFront(
		@RequestParam("productId") Integer productId,
		@RequestParam("quantity") Integer quantity,
		HttpSession session
	) {
		com.member.model.MemberVO member = (com.member.model.MemberVO) session.getAttribute("memberVO");
		if (member == null) {
			// 未登入，導向登入頁
			return "redirect:/login";
		}
		ProdCartVO cart = new ProdCartVO();
		ProdMemberIdVO pmid = new ProdMemberIdVO();
		pmid.setMemberId(member.getMemberId());
		pmid.setProductId(productId);
		cart.setPmid(pmid);
		cart.setMemberVO(member);
		ProdVO prod = new ProdVO();
		prod.setProductId(productId);
		cart.setProdVO(prod);
		cart.setQuantity(quantity);
		prodCartSvc.addProdCart(cart);
		// 回到商城首頁或購物車頁
		return "redirect:/front-end/shop";
	}
}