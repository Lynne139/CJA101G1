package com.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.prod.model.ProdService;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.resto.model.RestoService;
import com.resto.model.RestoVO;

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
    @GetMapping("/prodCate")
    public String prodCate(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prodCate";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    @GetMapping("/prodCart")
    public String shop3(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prodCart";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	return "admin/index_admin";
    } 
    @GetMapping("/prodPhoto")
    public String shop4(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/shop/prodPhoto";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

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
