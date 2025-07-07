package com.shopOrd.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.prod.model.ProdService;
import com.prod.model.ProdVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.shopOrd.model.ShopOrdService;
import com.shopOrd.model.ShopOrdVO;
import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import com.coupon.service.MemberCouponService;
import com.coupon.repository.MemberCouponRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
@Validated
@RequestMapping("/shopOrd")
public class ShopOrdController {
	
	@Autowired
	ShopOrdService shopOrdSvc;

	@Autowired
	MemberService memberSvc;
	
	@Autowired
	CouponService couponService;
	
	@Autowired
	MemberCouponService memberCouponService;
	
	@Autowired
	MemberCouponRepository memberCouponRepository;
	
	@GetMapping("/admin/shopOrd/select_page")
	public String selectPage(Model model) {
		model.addAttribute("shopOrdVO", new ShopOrdVO());
		model.addAttribute("prodCateListData", memberSvc.getAll());
		model.addAttribute("prodListData", shopOrdSvc.getAll());
		model.addAttribute("shopOrdListData", shopOrdSvc.getAll());
		model.addAttribute("mainFragment", "admin/fragments/shop/shopOrd/select_page :: content");
		return "admin/index_admin";
	}
	
	@GetMapping("listAllShopOrd")
	public String listAllShopOrd(Model model) {
	    List<ShopOrdVO> list = shopOrdSvc.getAll();
	    model.addAttribute("shopOrdListData", list);
	    return "admin/fragments/shop/shopOrd/listAllShopOrd";
	}

	@ModelAttribute("shopOrdListData")  // for select_page.html 第97 109行用 // for listAllEmp.html 第85行用
	protected List<ShopOrdVO> referenceListData(Model model) {
		
    	List<ShopOrdVO> list = shopOrdSvc.getAll();
		return list;
	}
    
//	@ModelAttribute("prodCateListData") // for select_page.html 第135行用
//	protected List<ProdCateVO> referenceListData_ProdCate(Model model) {
//		model.addAttribute("prodCateVO", new ProdCateVO()); // for select_page.html 第133行用
//		List<ProdCateVO> list = prodCateSvc.getAll();
//		return list;
//	}

	/*
	 * This method will be called on listAllEmp.html form submission, handling POST request
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("prodOrdId") String prodOrdId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		// EmpService empSvc = new EmpService();
		ShopOrdVO shopOrdVO = shopOrdSvc.getOneShopOrd(Integer.valueOf(prodOrdId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		if (shopOrdVO == null || shopOrdVO.getMemberVO() == null) {
			model.addAttribute("errorMessage", "查無此訂單或會員資料");
			model.addAttribute("couponListData", new java.util.ArrayList<>());
			model.addAttribute("couponMap", new java.util.HashMap<>());
			return "admin/fragments/shop/shopOrd/update_shopOrd_input";
		}
		model.addAttribute("shopOrdVO", shopOrdVO);

		// 查詢該會員已領取且可用的商城折價券
		Integer memberId = shopOrdVO.getMemberVO().getMemberId();
		Integer prodAmount = shopOrdVO.getProdAmount() != null ? shopOrdVO.getProdAmount() : 0;
		List<Coupon> couponList = memberCouponRepository.findProdOnlyOrAllCoupons(
			memberId,
			java.time.LocalDate.now(),
			prodAmount
		);
		if (couponList == null) couponList = new java.util.ArrayList<>();
		model.addAttribute("couponListData", couponList);

		// 新增：產生 couponMap 給前端
		java.util.Map<String, Integer> couponMap = couponList.stream()
			.collect(java.util.stream.Collectors.toMap(
				c -> c.getCouponCode(),
				c -> c.getDiscountValue()
			));
		model.addAttribute("couponMap", couponMap);

		return "admin/fragments/shop/shopOrd/update_shopOrd_input"; // 查詢完成後轉交update_emp_input.html
	}

	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 */
	@PostMapping("update")
	public String update(@Valid ShopOrdVO shopOrdVO, BindingResult result, ModelMap model) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/

		if (result.hasErrors()) {
			return "admin/fragments/shop/shopOrd/update_shopOrd_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		// EmpService empSvc = new EmpService();

		shopOrdSvc.updateShopOrd(shopOrdVO);
		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		// 取得修改後的商品ID，然後redirect到select_page並顯示該商品
		Integer newProdOrdId = shopOrdVO.getProdOrdId();
	    return "redirect:/admin/shopOrd/select_page?prodOrdId=" + newProdOrdId + "&success=true";
	}

	/**
	 * 從購物車建立訂單（結帳功能）
	 * 這是主要的訂單建立方式，符合電商流程
	 */
	@PostMapping("checkout")
	@ResponseBody
	public Map<String, Object> checkout(@RequestBody(required = false) Map<String, Object> requestData,
						  @RequestParam(value = "memberId", required = false) Integer memberIdParam,
						  @RequestParam(value = "couponCode", required = false) String couponCodeParam,
						  @RequestParam(value = "paymentMethod", required = false) Boolean paymentMethodParam,
						  HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			Integer memberId;
			String couponCode;
			Boolean paymentMethod;
			
			// 處理JSON請求或表單請求
			if (requestData != null) {
				// JSON請求 - 安全地處理類型轉換
				Object memberIdObj = requestData.get("memberId");
				if (memberIdObj instanceof Integer) {
					memberId = (Integer) memberIdObj;
				} else if (memberIdObj instanceof String) {
					try {
						memberId = Integer.valueOf((String) memberIdObj);
					} catch (NumberFormatException e) {
						memberId = null;
					}
				} else {
					memberId = null;
				}
				
				Object couponCodeObj = requestData.get("couponCode");
				couponCode = couponCodeObj instanceof String ? (String) couponCodeObj : null;
				
				Object paymentMethodObj = requestData.get("paymentMethod");
				if (paymentMethodObj instanceof Boolean) {
					paymentMethod = (Boolean) paymentMethodObj;
				} else if (paymentMethodObj instanceof String) {
					paymentMethod = Boolean.valueOf((String) paymentMethodObj);
				} else {
					paymentMethod = true; // 預設值
				}
			} else {
				// 表單請求
				memberId = memberIdParam;
				couponCode = couponCodeParam;
				paymentMethod = paymentMethodParam;
			}
			
			// 如果沒有提供會員ID，從session獲取
			if (memberId == null) {
				MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loggedInMember");
				if (memberVO != null) {
					memberId = memberVO.getMemberId();
				} else {
					response.put("success", false);
					response.put("message", "請先登入會員");
					return response;
				}
			}
			
			// 呼叫服務層的結帳方法
			shopOrdSvc.createOrderFromCart(memberId, couponCode, paymentMethod);
			
			response.put("success", true);
			response.put("message", "訂單建立成功");
			return response;
			
		} catch (RuntimeException e) {
			response.put("success", false);
			response.put("message", e.getMessage());
			return response;
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "系統錯誤，請稍後再試");
			return response;
		}
	}

	/**
	 * 手動新增訂單（管理員功能）
	 * 用於管理員手動建立訂單
	 */
	@GetMapping("addShopOrd")
	public String addShopOrd(Model model, HttpServletRequest request) {
		model.addAttribute("shopOrdVO", new ShopOrdVO());
		// 從session獲取當前登入的會員資訊
		MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loggedInMember");
		if (memberVO != null) {
			Integer memberId = memberVO.getMemberId();
			int memberPoints = memberVO.getMemberPoints() != null ? memberVO.getMemberPoints() : 0;
			
			// 查詢商城專用和通用優惠券
			List<Coupon> prodOnlyCoupons = couponService.getClaimableCouponsForMember(
				com.coupon.enums.OrderType.PROD_ONLY, memberId, memberPoints);
			List<Coupon> roomAndProdCoupons = couponService.getClaimableCouponsForMember(
				com.coupon.enums.OrderType.ROOM_AND_PROD, memberId, memberPoints);
			
			// 合併兩種類型的優惠券
			List<Coupon> allCoupons = new ArrayList<>();
			allCoupons.addAll(prodOnlyCoupons);
			allCoupons.addAll(roomAndProdCoupons);
			
			model.addAttribute("couponListData", allCoupons);
		} else {
			// 如果沒有登入會員，返回空列表
			model.addAttribute("couponListData", new ArrayList<>());
		}
		return "admin/fragments/shop/shopOrd/addShopOrd";
	}

	/**
	 * 手動新增訂單（管理員功能）
	 * 用於管理員手動建立訂單
	 */
	@PostMapping("shoprOrd/addShopOrd")
	public String addShopOrd(@Valid ShopOrdVO shopOrdVO, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "admin/fragments/shop/shopOrd/addShopOrd";
		}
		
		try {
			// 使用簡化版的新增方法，只建立主檔
			shopOrdSvc.addShopOrd(shopOrdVO);
			return "redirect:/admin/shopOrd/select_page?success=true&message=訂單建立成功";
		} catch (Exception e) {
			return "redirect:/admin/shopOrd/select_page?errorMessage=" + e.getMessage();
		}
	}

	/*
	 * 第一種作法 Method used to populate the List Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${deptListData}" itemValue="deptno" itemLabel="dname" />
	 */
	@ModelAttribute("memberListData")
	protected List<MemberVO> referenceListData() {
		// DeptService deptSvc = new DeptService();
		List<MemberVO> list = memberSvc.getAll();
		return list;
	}

	/*
	 * 【 第二種作法 】 Method used to populate the Map Data in view. 如 : 
	 * <form:select path="deptno" id="deptno" items="${depMapData}" />
	 */
//	@ModelAttribute("deptMapData") //
//	protected Map<Integer, String> referenceMapData() {
//		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
//		map.put(10, "財務部");
//		map.put(20, "研發部");
//		map.put(30, "業務部");
//		map.put(40, "生管部");
//		return map;
//	}

	// 去除BindingResult中某個欄位的FieldError紀錄
	public BindingResult removeFieldError(ShopOrdVO shopOrdVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(shopOrdVO, "shopOrdVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}
	
	/*
	 * This method will be called on select_page.html form submission, handling POST
	 * request It also validates the user input
	 */
	@GetMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam(value = "prodOrdId", required = false) String prodOrdId) {
	    if (prodOrdId == null || prodOrdId.trim().isEmpty()) {
	        return "redirect:/admin/shopOrd/select_page?errorMessage=訂單編號請勿空白";
	    }

	   
	        Integer id = Integer.valueOf(prodOrdId);
	        ShopOrdVO shopOrdVO = shopOrdSvc.getOneShopOrd(id);
	        if (shopOrdVO == null) {
	            return "redirect:/admin/shopOrd/select_page?errorMessage=查無資料";
	        } else {
	            return "redirect:/admin/shopOrd/select_page?prodOrdId=" + id;
	        }
	    
	}

	@ExceptionHandler(value = { ConstraintViolationException.class })
	public String handleError(HttpServletRequest req, ConstraintViolationException e, Model model) {
		Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
		StringBuilder strBuilder = new StringBuilder();
		for (ConstraintViolation<?> violation : violations ) {
			strBuilder.append(violation.getMessage()).append(" ");
		}
		String message = strBuilder.toString();
		return "redirect:/admin/shopOrd/select_page?errorMessage=" + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8);
	}

	/**
	 * 獲取會員可用的折價券列表（前台API）
	 */
	@GetMapping("api/available-coupons")
	@ResponseBody
	public Map<String, Object> getAvailableCoupons(
			@RequestParam(defaultValue = "0") Integer cartTotal,
			HttpServletRequest request) {
		Map<String, Object> response = new HashMap<>();
		
		try {
			System.out.println("=== 開始獲取可用折價券 ===");
			System.out.println("購物車總金額: " + cartTotal);
			System.out.println("Session ID: " + request.getSession().getId());
			
			// 從session獲取當前登入的會員資訊
			MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loggedInMember");
			if (memberVO == null) {
				System.out.println("會員未登入 - session中沒有memberVO");
				response.put("success", false);
				response.put("message", "請先登入會員");
				return response;
			}
			
			Integer memberId = memberVO.getMemberId();
			System.out.println("會員ID: " + memberId);
			System.out.println("會員名稱: " + memberVO.getMemberName());
			
			// 使用MemberCouponService查詢會員可用於商城訂單的折價券
			List<Coupon> availableCoupons = memberCouponService.getProductOrderApplicableCoupons(
				memberId, cartTotal);
			
			System.out.println("可用折價券數量: " + availableCoupons.size());
			for (Coupon coupon : availableCoupons) {
				System.out.println("折價券: " + coupon.getCouponCode() + " - " + coupon.getCouponName() + 
					" (類型: " + coupon.getOrderType().getLabel() + ", 折扣: " + coupon.getDiscountValue() + 
					", 最低消費: " + coupon.getMinPurchase() + ")");
			}
			System.out.println("=== 獲取折價券完成 ===");
			
			response.put("success", true);
			response.put("coupons", availableCoupons);
			return response;
			
		} catch (Exception e) {
			System.out.println("獲取折價券時發生錯誤: " + e.getMessage());
			e.printStackTrace();
			response.put("success", false);
			response.put("message", "獲取折價券失敗: " + e.getMessage());
			return response;
		}
	}

	/**
	 * 後台取消訂單（ordStat=2）
	 */
	@PostMapping("/admin/shopOrd/cancel")
	@ResponseBody
	public Map<String, Object> cancelOrder(@RequestParam("prodOrdId") Integer prodOrdId) {
		Map<String, Object> response = new HashMap<>();
		ShopOrdVO order = shopOrdSvc.getOneShopOrd(prodOrdId);
		if (order == null) {
			response.put("success", false);
			response.put("message", "查無此訂單");
			return response;
		}
		order.setOrdStat(2); // 2 代表取消
		shopOrdSvc.updateShopOrd(order);
		response.put("success", true);
		response.put("message", "訂單已取消");
		return response;
	}

	@GetMapping("/api/member/orders")
	@ResponseBody
	public List<Map<String, Object>> getMemberOrders(HttpSession session) {
		com.member.model.MemberVO member = (com.member.model.MemberVO) session.getAttribute("loggedInMember");
		if (member == null) return java.util.Collections.emptyList();
		List<com.shopOrd.model.ShopOrdVO> orders = shopOrdSvc.getOrdersByMemberId(member.getMemberId());
		return orders.stream().map(o -> {
			Map<String, Object> map = new java.util.HashMap<>();
			map.put("prodOrdId", o.getProdOrdId());
			map.put("prodOrdDate", o.getProdOrdDate());
			map.put("prodAmount", o.getProdAmount());
			map.put("payMethod", o.getPayMethod());
			map.put("ordStat", o.getOrdStat());
			return map;
		}).collect(java.util.stream.Collectors.toList());
	}

}
