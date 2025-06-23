package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.model.MemberService;
import com.prod.model.ProdService;
import com.prodCart.model.ProdCartService;
import com.prodCart.model.ProdCartVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;

import com.resto.model.RestoService;
import com.resto.model.RestoVO;
import com.room.model.RoomService;
import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;
import com.roomtypeschedule.model.RoomTypeScheduleVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class AdminIndexController {
	
	@Autowired
	RestoService restoService;
	
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
	RoomTypeService roomTypeService;
	
	@Autowired
	RoomService roomService;
	
	@Autowired
	RoomTypeScheduleService roomTypeScheduleService;
		
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
    // === 新增/查詢 ===
    @GetMapping("/staff1")
    public String staff1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/staff/staff1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    // === 權限管理 ===
    @GetMapping("/staff2")
    public String staff2(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/staff/staff2";
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
    @GetMapping("/listAllRoomType")
    public String listAllRoomType(HttpServletRequest request,HttpServletResponse response,Model model) {
    	
    	String mainFragment = "admin/fragments/room/listAllRoomType";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	List<RoomTypeVO> roomTypeVOList = roomTypeService.getAll();
    	model.addAttribute("roomTypeVOList",roomTypeVOList);
    	
    	return "admin/index_admin";
    } 
    @GetMapping("/listAllRoomTypeSchedule")
    public String listAllRoomTypeSchedule(HttpServletRequest request,HttpServletResponse response,Model model) {
    	
    	String mainFragment = "admin/fragments/room/listAllRoomTypeSchedule";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	List<RoomTypeScheduleVO> roomTypeScheduleVOList = roomTypeScheduleService.getAll();
    	model.addAttribute("roomTypeScheduleVOList",roomTypeScheduleVOList);
    	
    	return "admin/index_admin";
    } 
    @GetMapping("/listAllRoom")
    public String listAllRoom(HttpServletRequest request,HttpServletResponse response,Model model) {
    	
    	String mainFragment = "admin/fragments/room/listAllRoom";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	List<RoomVO> roomVOList = roomService.getAll();
    	model.addAttribute("roomVOList",roomVOList);
    	
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
    	
    	// 讓複合查詢欄位保持原值（用於 th:selected / th:value）
        for (String key : paramMap.keySet()) {
            model.addAttribute(key, paramMap.get(key)[0]);
        }

    	return "admin/index_admin";
    } 
    @GetMapping("/resto_timeslot")
    public String restoTimeslot(HttpServletRequest request,Model model) {
    	
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
    
    @GetMapping("/shopOrd")
    public String shop5(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/shopOrd";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    @GetMapping("/shopOrdDet")
    public String shop6(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/shopOrdDet";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    
    // === 優惠管理 ===
    @GetMapping("/coupon1")
    public String coupon1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/coupon/coupon1";
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
