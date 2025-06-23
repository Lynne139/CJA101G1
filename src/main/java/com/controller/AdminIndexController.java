package com.controller;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.prod.model.ProdService;
import com.prodCart.model.ProdCartService;
import com.prodCart.model.ProdCartVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;
import com.resto.model.PeriodService;
import com.resto.model.RestoService;
import com.resto.model.RestoVO;
import com.resto.model.TimeslotService;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;
import com.shopOrd.model.ShopOrdService;
import com.shopOrd.model.ShopOrdVO;
import com.shopOrdDet.model.ShopOrdDetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminIndexController {
	
	@Autowired
	RestoService restoService;
	@Autowired
	PeriodService periodService;
	@Autowired
	TimeslotService timeslotService;
	
	@Autowired
	ProdService prodSvc;
	
	@Autowired
	ProdCateService prodCateSvc;
	
	@Autowired
	ProdPhotoService prodPhotoSvc;
	
	@Autowired
	ProdCartService prodCartSvc;
	
	@Autowired
	MemberService memberSvc;
	
	@Autowired
	ShopOrdService shopOrdSvc;
	
	@Autowired
	CouponService couponSvc;
	
	@Autowired
	ShopOrdDetService shopOrdDetSvc;
	
	@Autowired
	RoomOrderService roomOrderService;
	
		
	// === 後台首頁 ===
    @GetMapping("")
    public String index(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/default";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
	
    
    // === 會員管理 ===
    @GetMapping("/member1")
    public String member1(HttpServletRequest request,Model model) {
    	String mainFragment = "admin/fragments/member/member1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	return "admin/index_admin";
    } 
    
    // === 員工管理 ===
    @GetMapping("/staff1")
    public String staff1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/staff/staff1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    
    // === 房間管理 ===
    @GetMapping("/room1")
    public String room1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/room/room1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";

    } 



	//===住宿訂單管理===
	@GetMapping("/roomo_info")
	public String roomoInfo(HttpServletRequest request,Model model) {

		String mainFragment = "admin/fragments/roomo/roomoInfo";
		model.addAttribute("mainFragment", mainFragment);
		model.addAttribute("currentURI", request.getRequestURI());

		// 複合查詢 + Datatables
		Map<String, String[]> paramMap = request.getParameterMap();
		List<RoomOrder> roomoList = roomOrderService.compositeQuery(paramMap);
		model.addAttribute("roomoList", roomoList);

        return "admin/index_admin";
    }

	// === 餐廳管理 ===

    @GetMapping("/resto_info")
    public String restoInfo(HttpServletRequest request,
    						HttpServletResponse response,
    						Model model) {

    	String mainFragment = "admin/fragments/resto/restoInfo";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 複合查詢 + Datatables
    	Map<String, String[]> paramMap = request.getParameterMap();
        List<RestoVO> restoList = restoService.compositeQuery(paramMap);
    	model.addAttribute("restoList", restoList);
    	
    	// 給下拉選單用的isEnabled
        Map<String, String> isEnabledOptions = new LinkedHashMap<>();
        isEnabledOptions.put("", "--- 所有狀態 ---"); // 空字串代表不篩選
        isEnabledOptions.put("true", "上架");
        isEnabledOptions.put("false", "下架");
        model.addAttribute("isEnabledOptions", isEnabledOptions);
    	
    	// 讓複合查詢欄位保持原值（用於 th:selected / th:value）
        for (String key : paramMap.keySet()) {
            model.addAttribute(key, paramMap.get(key)[0]);
        }

    	return "admin/index_admin";
    	} 
    
    	@GetMapping("/resto_timeslot")
    	public String restoTimeslot(HttpServletRequest request,
	        @RequestParam(value = "restoId", required = false) Integer restoId,
    		Model model) {
    		
		// 把所有餐廳都傳給下拉選單使用
        List<RestoVO> restoList = restoService.getAll();
        model.addAttribute("restoList", restoList);

        // 若選定某餐廳，才撈出該餐廳的區段與時段
        if (restoId != null) {
            model.addAttribute("selectedRestoId", restoId);
            model.addAttribute("periodList", periodService.getPeriodsByRestoId(restoId));
            model.addAttribute("timeslotList", timeslotService.getTimeslotsByRestoId(restoId));
            
           
        } else {
            model.addAttribute("selectedRestoId", null);
            // 在else中補空清單進入model，避免Null
            model.addAttribute("periodList", new ArrayList<>());
            model.addAttribute("timeslotList", new ArrayList<>());
        }
    	
    	String mainFragment = "admin/fragments/resto/restoTimeslot";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    	
    @GetMapping("/resto_order")
    public String restoOrder(HttpServletRequest request,Model model) {
    	
    	String mainFragment = "admin/fragments/resto/restoOrder";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    
    // === 商店管理 ===
    @GetMapping("/prod/select_page")
    public String prod(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prod/select_page";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加商品資料到 model 中
    	List<com.prod.model.ProdVO> list = prodSvc.getAll();
    	model.addAttribute("prodListData", list);
    	model.addAttribute("prodCateVO", new ProdCateVO());
    	List<com.prodCate.model.ProdCateVO> list2 = prodCateSvc.getAll();
    	model.addAttribute("prodCateListData", list2);
    	
    	// 檢查是否有錯誤訊息（來自 ProductIdController）
    	String errorMessage = request.getParameter("errorMessage");
    	if (errorMessage != null && !errorMessage.isEmpty()) {
    		model.addAttribute("errorMessage", errorMessage);
    	}
    	
    	// 檢查是否有查詢結果
    	String productId = request.getParameter("productId");
    	if (productId != null && !productId.isEmpty()) {
    		try {
    			com.prod.model.ProdVO prodVO = prodSvc.getOneProd(Integer.valueOf(productId));
    			if (prodVO != null) {
    				model.addAttribute("prodVO", prodVO);
    			} else {
    				model.addAttribute("errorMessage", "查無資料");
    			}
    		} catch (NumberFormatException e) {
    			model.addAttribute("errorMessage", "商品編號格式錯誤");
    		}
    	}

    	return "admin/index_admin";
    } 
    @GetMapping("/prodCate/select_page")
    public String prodCateSelectPage(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prodCate/select_page";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加商品分類資料到 model 中
    	List<com.prodCate.model.ProdCateVO> list = prodCateSvc.getAll();
    	model.addAttribute("prodCateListData", list);
    	
    	// 檢查是否有錯誤訊息
    	String errorMessage = request.getParameter("errorMessage");
    	if (errorMessage != null && !errorMessage.isEmpty()) {
    		model.addAttribute("errorMessage", errorMessage);
    	}
    	
    	// 檢查是否有查詢結果
    	String prodCateId = request.getParameter("prodCateId");
    	if (prodCateId != null && !prodCateId.isEmpty()) {
    		try {
    			com.prodCate.model.ProdCateVO prodCateVO = prodCateSvc.getOneProdCate(Integer.valueOf(prodCateId));
    			if (prodCateVO != null) {
    				model.addAttribute("prodCateVO", prodCateVO);
    			} else {
    				model.addAttribute("errorMessage", "查無資料");
    			}
    		} catch (NumberFormatException e) {
    			model.addAttribute("errorMessage", "商品分類編號格式錯誤");
    		}
    	}

    	return "admin/index_admin";
    }
    
    @GetMapping("/prodPhoto/select_page")
	public String prodPhotoselectPage(HttpServletRequest request, Model model) {

		String mainFragment = "admin/fragments/shop/prodPhoto/select_page";
		model.addAttribute("mainFragment", mainFragment);
		model.addAttribute("currentURI", request.getRequestURI());
		
		// 添加商品照片資料到 model 中
		List<com.prodPhoto.model.ProdPhotoVO> list = prodPhotoSvc.getAll();
    	model.addAttribute("prodPhotoListData", list);

		List<com.prod.model.ProdVO> list2 = prodSvc.getAll();
		model.addAttribute("prodListData", list2);
		
		// 檢查是否有錯誤訊息
		String errorMessage = request.getParameter("errorMessage");
		if (errorMessage != null && !errorMessage.isEmpty()) {
			model.addAttribute("errorMessage", errorMessage);
		}
		
		// 檢查是否有查詢結果
		String prodPhotoId = request.getParameter("prodPhotoId");
		if (prodPhotoId != null && !prodPhotoId.isEmpty()) {
			try {
				ProdPhotoVO prodPhotoVO = prodPhotoSvc.getOneProdPhoto(Integer.valueOf(prodPhotoId));
				if (prodPhotoVO != null) {
					model.addAttribute("prodPhotoVO", prodPhotoVO);
				} else {
					model.addAttribute("errorMessage", "查無資料");
				}
			} catch (NumberFormatException e) {
				model.addAttribute("errorMessage", "商品照片編號格式錯誤");
			}
		}
		
		return "admin/index_admin";
	}
    
    @GetMapping("/prodCart/select_page")
    public String prodCartselectPage(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prodCart/select_page";
		model.addAttribute("mainFragment", mainFragment);
		model.addAttribute("currentURI", request.getRequestURI());
		
		// 添加購物車資料到 model 中
		List<ProdCartVO> list = prodCartSvc.getAll();
    	model.addAttribute("prodCartListData", list);
		
		// 添加會員資料到 model 中
		List<com.member.model.MemberVO> memberList = memberSvc.getAll();
    	model.addAttribute("memberListData", memberList);
    	
    	// 添加商品資料到 model 中
		List<com.prod.model.ProdVO> prodList = prodSvc.getAll();
		model.addAttribute("prodListData", prodList);
		
		// 檢查是否有錯誤訊息
		String errorMessage = request.getParameter("errorMessage");
		if (errorMessage != null && !errorMessage.isEmpty()) {
			model.addAttribute("errorMessage", errorMessage);
		}
		
		// 檢查 Session 中是否有表單資料（來自驗證錯誤）
		ProdCartVO formData = (ProdCartVO) request.getSession().getAttribute("formData");
		if (formData != null) {
			// 將表單資料轉換為 JSON 字串，傳遞給前端 JavaScript
			ObjectMapper mapper = new ObjectMapper();
			try {
				String formDataJson = mapper.writeValueAsString(formData);
				model.addAttribute("formDataJson", formDataJson);
			} catch (Exception e) {
				// 如果轉換失敗，忽略錯誤
			}
			// 清除 Session 中的表單資料
			request.getSession().removeAttribute("formData");
			request.getSession().removeAttribute("formErrors");
		}
		
		// 檢查是否有查詢結果（複合主鍵查詢）
		String productId = request.getParameter("productId");
		String memberId = request.getParameter("memberId");
		if (productId != null && !productId.isEmpty() && memberId != null && !memberId.isEmpty()) {
			try {
				ProdCartVO prodCartVO = prodCartSvc.getOneProdCart(Integer.valueOf(productId), Integer.valueOf(memberId));
				if (prodCartVO != null) {
					model.addAttribute("prodCartVO", prodCartVO);
				} else {
					model.addAttribute("errorMessage", "查無資料");
				}
			} catch (NumberFormatException e) {
				model.addAttribute("errorMessage", "商品編號或會員編號格式錯誤");
			}
		}
		
		return "admin/index_admin";
    } 
    
    @GetMapping("/shopOrd/select_page")
    public String shop5(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/shopOrd/select_page";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加商品資料到 model 中
    	List<com.shopOrd.model.ShopOrdVO> list = shopOrdSvc.getAll();
    	model.addAttribute("shopOrdListData", list);
    	model.addAttribute("memberVO", new MemberVO());
    	
    	// 從session獲取當前登入的會員資訊
    	MemberVO memberVO = (MemberVO) request.getSession().getAttribute("memberVO");
    	if (memberVO != null) {
    		Integer memberId = memberVO.getMemberId();
    		int memberPoints = memberVO.getMemberPoints() != null ? memberVO.getMemberPoints() : 0;
    		
    		// 查詢商城專用和通用優惠券
    		List<Coupon> prodOnlyCoupons = couponSvc.getClaimableCouponsForMember(
    			com.coupon.enums.OrderType.PROD_ONLY, memberId, memberPoints);
    		List<Coupon> roomAndProdCoupons = couponSvc.getClaimableCouponsForMember(
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
    	
    	// 檢查是否有錯誤訊息（來自 ProductIdController）
    	String errorMessage = request.getParameter("errorMessage");
    	if (errorMessage != null && !errorMessage.isEmpty()) {
    		model.addAttribute("errorMessage", errorMessage);
    	}
    	
    	// 檢查是否有查詢結果
    	String prodOrdId = request.getParameter("prodOrdId");
    	if (prodOrdId != null && !prodOrdId.isEmpty()) {
    		try {
    			ShopOrdVO shopOrdVO = shopOrdSvc.getOneShopOrd(Integer.valueOf(prodOrdId));
    			if (shopOrdVO != null) {
    				model.addAttribute("shopOrdVO", shopOrdVO);
    			} else {
    				model.addAttribute("errorMessage", "查無資料");
    			}
    		} catch (NumberFormatException e) {
    			model.addAttribute("errorMessage", "訂單編號格式錯誤");
    		}
    	}

    	return "admin/index_admin";
    }
    
    @GetMapping("/shopOrd/addShopOrd")
    public String addShopOrd(HttpServletRequest request, Model model) {
    	String mainFragment = "admin/fragments/shop/shopOrd/addShopOrd";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加訂單資料到 model 中
    	model.addAttribute("shopOrdVO", new com.shopOrd.model.ShopOrdVO());
    	
    	// 從session獲取當前登入的會員資訊
    	MemberVO memberVO = (MemberVO) request.getSession().getAttribute("memberVO");
    	if (memberVO != null) {
    		Integer memberId = memberVO.getMemberId();
    		int memberPoints = memberVO.getMemberPoints() != null ? memberVO.getMemberPoints() : 0;
    		
    		// 查詢商城專用和通用優惠券
    		List<Coupon> prodOnlyCoupons = couponSvc.getClaimableCouponsForMember(
    			com.coupon.enums.OrderType.PROD_ONLY, memberId, memberPoints);
    		List<Coupon> roomAndProdCoupons = couponSvc.getClaimableCouponsForMember(
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
    	
    	return "admin/index_admin";
    }
    
    @GetMapping("/shopOrdDet/select_page")
    public String shop6(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/shopOrdDet/select_page";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加訂單明細資料到 model 中
    	List<com.shopOrdDet.model.ShopOrdDetVO> list = shopOrdDetSvc.getAll();
    	model.addAttribute("shopOrdDetListData", list);
    	
    	// 添加商品資料到 model 中
    	List<com.prod.model.ProdVO> prodList = prodSvc.getAll();
    	model.addAttribute("prodListData", prodList);
    	
    	// 添加訂單資料到 model 中
    	List<com.shopOrd.model.ShopOrdVO> shopOrdList = shopOrdSvc.getAll();
    	model.addAttribute("shopOrdListData", shopOrdList);
    	
    	// 檢查是否有錯誤訊息
    	String errorMessage = request.getParameter("errorMessage");
    	if (errorMessage != null && !errorMessage.isEmpty()) {
    		model.addAttribute("errorMessage", errorMessage);
    	}
    	
    	// 檢查是否有查詢結果（複合主鍵查詢）
    	String prodOrdId = request.getParameter("prodOrdId");
    	String productId = request.getParameter("productId");
    	if (prodOrdId != null && !prodOrdId.isEmpty() && productId != null && !productId.isEmpty()) {
    		try {
    			com.shopOrdDet.model.ShopOrdDetVO shopOrdDetVO = shopOrdDetSvc.getOneShopOrdDet(Integer.valueOf(prodOrdId), Integer.valueOf(productId));
    			if (shopOrdDetVO != null) {
    				model.addAttribute("shopOrdDetVO", shopOrdDetVO);
    			} else {
    				model.addAttribute("errorMessage", "查無資料");
    			}
    		} catch (NumberFormatException e) {
    			model.addAttribute("errorMessage", "訂單編號或商品編號格式錯誤");
    		}
    	}

    	return "admin/index_admin";
    } 
    
    // === 優惠管理 ===
    @GetMapping("/coupon/select")
    public String couponSelectPage(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/coupon/admin-select-coupon";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    
    // === 客服管理 ===
    @GetMapping("/cs1")
    public String cs1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/cs/cs1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    
    // === 消息管理 ===
    @GetMapping("/news1")
    public String news1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/news/news1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
	
}
