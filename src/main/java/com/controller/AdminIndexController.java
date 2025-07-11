package com.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import com.employee.entity.Employee;
import com.employee.entity.JobTitle;
import com.employee.entity.Role;
import com.employee.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.memberLevelType.model.MemberLevelType;
import com.memberLevelType.model.MemberLevelTypeService;
import com.news.service.HotNewsService;
import com.news.service.NewsService;
import com.news.service.PromotionNewsService;
import com.prod.model.ProdService;
import com.prodCart.model.ProdCartService;
import com.prodCart.model.ProdCartVO;
import com.prodCate.model.ProdCateService;
import com.prodCate.model.ProdCateVO;
import com.prodPhoto.model.ProdPhotoService;
import com.prodPhoto.model.ProdPhotoVO;
import com.resto.dto.RestoDTO;
import com.resto.dto.RestoOrderDTO;
import com.resto.dto.RestoOrderSummaryDTO;
import com.resto.entity.PeriodVO;
import com.resto.entity.RestoReservationVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.PeriodService;
import com.resto.model.ReservationService;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderStatus;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;
import com.shopOrd.model.ShopOrdService;
import com.shopOrd.model.ShopOrdVO;
import com.shopOrdDet.model.ShopOrdDetService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;


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
	RestoOrderService restoOrderService;
	@Autowired
	ReservationService reservationService;
	
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
    private MemberLevelTypeService memberLevelTypeSvc;
    
    @Autowired
    private EmployeeService employeeService;
    
	@Autowired
	ShopOrdService shopOrdSvc;
	
	@Autowired
	CouponService couponSvc;
	
	@Autowired
	ShopOrdDetService shopOrdDetSvc;
	
	@Autowired
	RoomOrderService roomOrderService;
	
	@Autowired
	HotNewsService hotNewsSvc;
	
	@Autowired
	PromotionNewsService promotionNewsSvc;
	
	@Autowired
	NewsService newsSvc;
	
		
	// === 後台首頁 ===
    @GetMapping("")
    public String index(HttpServletRequest request,Model model) {
        // 不再覆蓋session資訊，讓AdminPermissionFilter來處理權限
        String mainFragment = "admin/fragments/default";
        model.addAttribute("mainFragment", mainFragment);
        model.addAttribute("currentURI", request.getRequestURI());
        return "admin/index_admin";
    } 
	
    
	 // === 會員管理 ===
	
	 // 查詢頁面
	 @GetMapping("/select_page")
	 public String showSelectPage(Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = memberSvc.getAll();
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/select_page");
	     return "admin/index_admin";
	 }
	
	 // 查詢 by ID
	 @PostMapping("/searchById")
	 public String searchById(@RequestParam("memberId") String memberId,
	                          Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = new ArrayList<>();
	     try {
	         MemberVO memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));
	         if (memberVO != null) memberList.add(memberVO);
	     } catch (NumberFormatException e) {
	         model.addAttribute("error", "會員編號格式錯誤");
	     }
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 查詢 by 姓名
	 @PostMapping("/searchByName")
	 public String searchByName(@RequestParam("memberName") String memberName,
	                            Model model, HttpServletRequest request) {
	     List<MemberVO> memberList = memberSvc.findByNameLike(memberName);
	     model.addAttribute("memberListData", memberList);
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 新增會員畫面
	 @GetMapping("/addMember")
	 public String addMember(Model model, HttpServletRequest request) {
	     model.addAttribute("memberVO", new MemberVO());
	     model.addAttribute("memberListData", memberSvc.getAll());
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/addMember");
	     return "admin/index_admin";
	 }
	
	 // 新增會員功能
	 @PostMapping("/insert")
	 public String insert(@ModelAttribute("memberVO") @Valid MemberVO memberVO,
	                      BindingResult result,
	                      @RequestParam("uploadPic") MultipartFile file,
	                      Model model, HttpServletRequest request) {
		 if (memberSvc.findByEmail(memberVO.getMemberEmail()) != null) {
		        result.rejectValue("memberEmail", null, "電子信箱已註冊，請使用其他信箱");

		        model.addAttribute("memberVO", memberVO);
		        model.addAttribute("memberListData", memberSvc.getAll());
		        model.addAttribute("currentURI", request.getRequestURI());
		        model.addAttribute("mainFragment", "admin/fragments/member/addMember");
		        return "admin/index_admin";
		    }
		 
	     if (result.hasErrors()) {
	    	 model.addAttribute("memberVO", memberVO);
	         model.addAttribute("memberListData", memberSvc.getAll());
	         model.addAttribute("currentURI", request.getRequestURI());
	         model.addAttribute("mainFragment", "admin/fragments/member/addMember");
	         return "admin/fragments/member/addMember";
	     }
	
	     try {
	         if (!file.isEmpty()) {
	             memberVO.setMemberPic(file.getBytes());
	         }
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	
	     memberSvc.addMember(memberVO);
	     return "redirect:/admin/listAllMember";
	 }
	
	 // 會員列表
	 @GetMapping("/listAllMember")
	 public String listAllMember(Model model, HttpServletRequest request) {
	     model.addAttribute("memberListData", memberSvc.getAll());
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/listAllMember");
	     return "admin/index_admin";
	 }
	
	 // 修改會員畫面
	 @GetMapping("/updateMember/{memberId}")
	 public String getUpdateMemberPage(@PathVariable("memberId") Integer memberId,
	                                   Model model, HttpServletRequest request) {
	     model.addAttribute("memberVO", memberSvc.getOneMember(memberId));
	     model.addAttribute("currentURI", request.getRequestURI());
	     model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	     return "admin/index_admin";
	 }
	
	 // 更新會員功能
	 @PostMapping("/update")
	 public String update(@Valid @ModelAttribute("memberVO") MemberVO memberVO,
	                      BindingResult result,
	                      @RequestParam("uploadPic") MultipartFile uploadPic,
	                      Model model, HttpServletRequest request) {
		 MemberVO existingMemberByEmail = memberSvc.findByEmail(memberVO.getMemberEmail());
		 if (existingMemberByEmail != null && !existingMemberByEmail.getMemberId().equals(memberVO.getMemberId())) {
		     result.rejectValue("memberEmail", null, "電子信箱已註冊，請使用其他信箱");
		     model.addAttribute("memberVO", memberVO);
		     model.addAttribute("currentURI", request.getRequestURI());
		     model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
		     return "admin/index_admin";
		 }
		 
		 if (result.hasErrors()) {
	         model.addAttribute("memberVO", memberVO);
	         model.addAttribute("currentURI", request.getRequestURI());
	         model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	         return "admin/index_admin";
	     }
	
	     try {
	         MemberVO existingMember = memberSvc.getOneMember(memberVO.getMemberId());
	
	         if (uploadPic != null && !uploadPic.isEmpty()) {
	             memberVO.setMemberPic(uploadPic.getBytes());
	         } else {
	             memberVO.setMemberPic(existingMember.getMemberPic());
	         }
	
	         memberSvc.updateMember(memberVO);
	
	     } catch (IOException e) {
	         model.addAttribute("errorMessage", "圖片上傳失敗：" + e.getMessage());
	         model.addAttribute("memberVO", memberVO);
	         model.addAttribute("currentURI", request.getRequestURI());
	         model.addAttribute("mainFragment", "admin/fragments/member/update_member_input");
	         return "admin/index_admin";
	     }
	
	     return "redirect:/admin/listAllMember";
	 }
	
	 // 刪除會員功能
	 @PostMapping("/delete")
	 public String delete(@RequestParam("memberId") String memberId,
	                      Model model) {
	     memberSvc.deleteMember(Integer.valueOf(memberId));
	     model.addAttribute("success", "- (刪除成功)");
	     return "redirect:/admin/listAllMember";
	 }
	// === 會員等級 ===
	// 顯示新增表單
	    @GetMapping("/addMemberLevelType")
	    public String addMemberLevelType(Model model, HttpServletRequest request) {
	    	model.addAttribute("memberLevelType", new MemberLevelType());
	    	model.addAttribute("memberLevelTypeList", memberLevelTypeSvc.getAll());
		    model.addAttribute("currentURI", request.getRequestURI());
		    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/addMemberLevelType");
		    return "admin/index_admin";
	    }

	    // 處理新增
	    @PostMapping("/insertL")
	    public String insertMemberLevelType(@ModelAttribute("memberLevelType") @Valid MemberLevelType memberLevelType,
	    					BindingResult result,
	    					Model model) {
	        if (result.hasErrors()) {
	        	model.addAttribute("memberLevelType", memberLevelType);
	            return "back-end/memberLevelType/addMemberLevelType";
	        }
	        memberLevelTypeSvc.addMemberLevelType(memberLevelType);
	        return "redirect:/admin/listAllMemberLevelType";
	    }

	    // 顯示全部資料
	    @GetMapping("/listAllMemberLevelType")
	    public String listAllMemberLevelType(Model model, HttpServletRequest request) {
	    	model.addAttribute("memberLevelTypeList", memberLevelTypeSvc.getAll());
		    model.addAttribute("currentURI", request.getRequestURI());
		    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/listAllMemberLevelType");
		    return "admin/index_admin";
	    }

	    // 修改資料畫面
	    @GetMapping("/updateMemberLevelType/{memberLevel}")
	    public String getUpdateMemberLevelTypePage(@PathVariable("memberLevel") String memberLevel,
	                                               Model model,
	                                               HttpServletRequest request) {
	        MemberLevelType memberLevelTypeList = memberLevelTypeSvc.getOneMemberLevelType(memberLevel);
	        model.addAttribute("memberLevelType", memberLevelTypeList);
	        model.addAttribute("currentURI", request.getRequestURI());
	        model.addAttribute("mainFragment", "admin/fragments/memberLevelType/update_memberLevelType_input");
	        return "admin/index_admin";
	    }

	    
	    // 更新資料
	    @PostMapping("/updateL")
	    public String updateMmemberLevel(@Valid @ModelAttribute("memberLevelType") MemberLevelType memberLevelType, 
	    					BindingResult result,
	    					Model model, HttpServletRequest request) {
	        if (result.hasErrors()) {
	        	model.addAttribute("memberLevelType", memberLevelType);
	    	    model.addAttribute("currentURI", request.getRequestURI());
	    	    model.addAttribute("mainFragment", "admin/fragments/memberLevelType/update_memberLevelType_input");
	    	    return "admin/index_admin";
	        }
	        
	        return "redirect:/admin/listAllMemberLevelType";
	    }

	    // 處理刪除
	    @PostMapping("/deleteL")
	    public String deleteMmemberLevel(@RequestParam("memberLevel") String memberLevel, 
	    									Model model) {
	        memberLevelTypeSvc.deleteMemberLevelType(memberLevel);
	        model.addAttribute("success", "- (刪除成功)");
	        return "redirect:/admin/listAllMemberLevelType";
	    }
	 
    // === 員工管理 ===
    @GetMapping("/staff1")
    public String staff1(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/staff/staff1";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	// 載入部門和職稱資料傳給前端
        try {
            List<Role> roleList = employeeService.getAllRoles();
            List<JobTitle> jobTitleList = employeeService.getAllJobTitles();
            model.addAttribute("roleList", roleList);
            model.addAttribute("jobTitleList", jobTitleList);
        } catch (Exception e) {
            // 如果載入失敗，至少傳送空的列表避免前端錯誤
            model.addAttribute("roleList", new ArrayList<>());
            model.addAttribute("jobTitleList", new ArrayList<>());
        }

    	return "admin/index_admin";
    } 
    
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
        List<RestoDTO> restoList = restoService.compositeQueryAsDTO(paramMap);
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
    								Model model
    	) {

		// 把所有餐廳都傳給下拉選單使用
        List<RestoVO> restoList = restoService.getAll();
        model.addAttribute("restoList", restoList);

        // 若選定某餐廳，才撈出該餐廳的區段與時段
        if (restoId != null) {
        	
        	// 軟刪除的餐廳只讀
            RestoVO Urlresto = restoService.getById(restoId);
        	boolean readonly = Urlresto.getIsDeleted();  
            model.addAttribute("readonly", readonly);
        	
        	// 把選到的餐廳的詳細資訊放進 model 以顯示餐廳名稱
            RestoVO selectedResto = restoService.getById(restoId);
            model.addAttribute("selectedResto", selectedResto);
            model.addAttribute("selectedRestoId", restoId);
            
            // 該餐廳的所有區段（period）與時段（timeslot）
            List<PeriodVO> periodList = periodService.getPeriodsByRestoId(restoId);
            List<TimeslotVO> timeslotList = timeslotService.getTimeslotsByRestoId(restoId);
            model.addAttribute("periodList", periodList);
            model.addAttribute("timeslotList", timeslotList);
            
            // 判斷區段有無綁定任何timeslot的period
            Map<Integer, Boolean> deletableMap = new HashMap<>();
            for (PeriodVO period : periodList) {
                boolean hasTimeslot = timeslotList.stream()
                    .anyMatch(ts -> ts.getPeriodVO() != null && ts.getPeriodVO().getPeriodId().equals(period.getPeriodId()));
                deletableMap.put(period.getPeriodId(), !hasTimeslot);
            }
            model.addAttribute("deletableMap", deletableMap);
        } else {
        	// 若沒選餐廳，仍補空值避免渲染錯誤
            model.addAttribute("selectedResto", null);
            model.addAttribute("periodList", new ArrayList<>());
            model.addAttribute("timeslotList", new ArrayList<>());
            model.addAttribute("readonly", false);
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
    	
    	// 複合查詢 + Datatables
    	Map<String, String[]> paramMap = request.getParameterMap();
        List<RestoOrderDTO> orderList = restoOrderService.compositeQueryAsDTO(paramMap);
        model.addAttribute("orderList", orderList);
        
    	// 給下拉選單用
        model.addAttribute("orderSourceOptions", List.of(RestoOrderSource.values()));
        model.addAttribute("orderStatusOptions", List.of(RestoOrderStatus.values()));
        
        // 下拉選單保持原值(因為param取得的字串與enum物件無法比較會抓不到select)
        String srcParam = request.getParameter("orderSource");
        if (StringUtils.hasText(srcParam)) {
            model.addAttribute("orderSource", RestoOrderSource.valueOf(srcParam));
        }
        String stParam  = request.getParameter("orderStatus");
        if (StringUtils.hasText(stParam)) {
            model.addAttribute("orderStatus", RestoOrderStatus.valueOf(stParam));
        }
        
    	// 讓複合查詢欄位保持原值（th:value）
        Set<String> enumKeys = Set.of("orderSource", "orderStatus");
        for (String key : paramMap.keySet()) {
            if (enumKeys.contains(key)) continue;

            String[] values = paramMap.get(key);
            if (values != null && values.length > 0 && StringUtils.hasText(values[0])) {
                model.addAttribute(key, values[0]);
            }
        }

    	return "admin/index_admin";
    } 
    
    @GetMapping("/resto_reservation")
    public String restoReservation(HttpServletRequest request,
    							   HttpServletResponse response,
    							   Model model) {

    	String mainFragment = "admin/fragments/resto/restoReservation";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());

    	// 複合查詢 + Datatables
    	Map<String, String[]> paramMap = request.getParameterMap();
        List<RestoReservationVO> restoRsvtList = reservationService.compositeQuery(paramMap);
    	model.addAttribute("restoRsvtList", restoRsvtList);
    	
    	// 把所有餐廳都傳給下拉選單使用
        List<RestoVO> restoList = restoService.getAll();
        model.addAttribute("restoList", restoList);
        
        
        // 讓複合查詢欄位保持原值（用於 th:selected / th:value）
        for (String key : paramMap.keySet()) {
            model.addAttribute(key, paramMap.get(key)[0]);
        }
        
    	
    	return "admin/index_admin";

    } 

    @GetMapping("/resto_order_today")
    public String restoOrderToday(HttpServletRequest request,
    							  @RequestParam(value = "restoId", required = false) Integer restoId,
								  Model model) {
    	
    	// 把所有餐廳都傳給下拉選單使用
        List<RestoVO> restoList = restoService.getAll();
        model.addAttribute("restoList", restoList);
        
        
    	// 餐廳總統計
    	List<RestoOrderSummaryDTO> summaryList = restoOrderService.getAllTodaySummaryPerResto();
    	model.addAttribute("summaryList", summaryList);
    	
    	long allTotal = summaryList.stream()
    		    .mapToLong(RestoOrderSummaryDTO::getTotal)
    		    .sum();
    		model.addAttribute("allTotal", allTotal);
        
        

        // 若選定某餐廳，才撈出該餐廳的區段與時段
        if (restoId != null) {
        	
        	// 各間餐廳統計
        	if (restoId != null) {
        	    RestoOrderSummaryDTO summary = restoOrderService.getTodaySummary(restoId);

        	    if (summary == null) {
        	        summary = new RestoOrderSummaryDTO(0L, 0L, 0L, 0L, 0L, 0L);
        	    }

        	    model.addAttribute("summary", summary);
        	}
        	

        	// 軟刪除的餐廳只讀
            RestoVO Urlresto = restoService.getById(restoId);
        	boolean readonly = Urlresto.getIsDeleted();  
            model.addAttribute("readonly", readonly);
        	
        	// 把選到的餐廳的詳細資訊放進 model 以顯示餐廳名稱
            RestoVO selectedResto = restoService.getById(restoId);
            model.addAttribute("selectedResto", selectedResto);
            model.addAttribute("selectedRestoId", restoId);
            
           // 該餐廳的所有今日訂單
            model.addAttribute("orderList", restoOrderService.findTodayOrders(restoId));
            
            
        } else {
        	// 若沒選餐廳，仍補空值避免渲染錯誤
            model.addAttribute("selectedResto", null);
            model.addAttribute("readonly", false);
        }
    	
    	
    	
    	String mainFragment = "admin/fragments/resto/restoOrderToday";
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
    	MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loggedInMember");
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
    	MemberVO memberVO = (MemberVO) request.getSession().getAttribute("loggedInMember");
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
    	
    	// 添加唯一訂單編號清單
    	Set<Integer> uniqueProdOrdIdList = list.stream()
    		.map(vo -> vo.getShopOrdVO().getProdOrdId())
    		.collect(Collectors.toCollection(LinkedHashSet::new));
    	model.addAttribute("uniqueProdOrdIdList", uniqueProdOrdIdList);
    	// 添加唯一商品編號清單
    	Set<Integer> uniqueProductIdList = list.stream()
    		.map(vo -> vo.getProdVO().getProductId())
    		.collect(Collectors.toCollection(LinkedHashSet::new));
    	model.addAttribute("uniqueProductIdList", uniqueProductIdList);
    	
    	// 構建 orderToProducts Map
    	Map<Integer, List<Map<String, Object>>> orderToProducts = new LinkedHashMap<>();
    	for (com.shopOrdDet.model.ShopOrdDetVO vo : list) {
    		Integer orderId = vo.getShopOrdVO().getProdOrdId();
    		Integer productId = vo.getProdVO().getProductId();
    		String productName = vo.getProdVO().getProductName();
    		orderToProducts.computeIfAbsent(orderId, k -> new ArrayList<>())
    			.add(Map.of("productId", productId, "productName", productName));
    	}
    	try {
    		ObjectMapper objectMapper = new ObjectMapper();
    		String orderToProductsJson = objectMapper.writeValueAsString(orderToProducts);
    		model.addAttribute("orderToProductsJson", orderToProductsJson);
    	} catch (Exception e) {
    		model.addAttribute("orderToProductsJson", "{}");
    	}
    	
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
    @GetMapping("/cs/select")
    public String csSelectPage(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/cs/select-cs";
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
    	
    	// 添加最新消息資料到 model 中
    	List<com.news.entity.HotNews> hotNewsList = hotNewsSvc.findAll();
    	model.addAttribute("hotNewsList", hotNewsList);
    	model.addAttribute("hotNews", new com.news.entity.HotNews());

    	return "admin/index_admin";
    } 
    
    @GetMapping("/news2")
    public String news2(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/news/news2";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加活動消息資料到 model 中
    	List<com.news.entity.PromotionNews> promotionNewsList = promotionNewsSvc.findAll();
    	model.addAttribute("promotionNewsList", promotionNewsList);
    	model.addAttribute("promotionNews", new com.news.entity.PromotionNews());

    	return "admin/index_admin";
    } 
    
    @GetMapping("/news3")
    public String news3(HttpServletRequest request,Model model) {

    	String mainFragment = "admin/fragments/news/news3";
    	model.addAttribute("mainFragment", mainFragment);
    	model.addAttribute("currentURI", request.getRequestURI());
    	
    	// 添加媒體消息資料到 model 中
    	List<com.news.entity.News> newsList = newsSvc.findAll();
    	model.addAttribute("newsList", newsList);
    	model.addAttribute("news", new com.news.entity.News());

    	return "admin/index_admin";
    } 
	
	@GetMapping("/test-permissions")
	@ResponseBody
	public Map<String, Object> testPermissions(HttpServletRequest request) {
		HttpSession session = request.getSession();
		Employee currentEmployee = (Employee) session.getAttribute("currentEmployee");
		List<String> permissions = (List<String>) session.getAttribute("employeePermissions");
		
		Map<String, Object> result = new HashMap<>();
		result.put("employeeId", currentEmployee != null ? currentEmployee.getEmployeeId() : null);
		result.put("employeeName", currentEmployee != null ? currentEmployee.getName() : null);
		result.put("permissions", permissions);
		return result;
	}
	
}
