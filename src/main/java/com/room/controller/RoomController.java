package com.room.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomNumberEditor;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.room.model.RoomService;
import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/admin")
public class RoomController {

	@Autowired
	RoomService roomSvc;

	@Autowired
	RoomTypeService roomTypeSvc;

	// 搜尋：不帶 Save 群組，就不會驗 roomId
	/**
	 * 1) 把所有 String trim 後全空白的變 null（方便判空／Pattern） 2) 忽略型別轉換錯誤（讓它只在 BindingResult
	 * 留下 error，繼續呼叫方法）
	 */
//	@InitBinder
//	public void initBinder(WebDataBinder binder) {
//	  // 轉空字串成 null
//	  binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
//	  // 遇到非數字型別轉換也不要直接拋錯，放到 BindingResult
//	  binder.registerCustomEditor(Integer.class,
//	    new CustomNumberEditor(Integer.class, /* allowEmpty */ true));
//	  binder.registerCustomEditor(Byte.class,
//	    new CustomNumberEditor(Byte.class, /* allowEmpty */ true));
//	// 把預設的 Validator 換成 LocalValidatorFactoryBean，
//	  // 讓 Spring MVC 能讀取你在 VO 上用的 JSR-303 註解 (如 @NotNull, @Min, @Pattern…)
//	  binder.setValidator(new LocalValidatorFactoryBean());
//	}

	/**
	 * 1. 點「房間資訊」側邊攔或 AJAX 查詢時，都走這裡 2. 首次進來 or 直接 GET /admin/listAllRoom 都是第一次進入頁面
	 * 3. AJAX 查詢只回傳 TABLE 的 fragment
	 */
	@GetMapping("/listAllRoom")
	public String listAllRoom(HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("roomVO") RoomVO roomVO, BindingResult result, Model model) {
		// 1. 下拉清單、sidebar
		model.addAttribute("currentURI", request.getRequestURI());
		model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());

		// 2. 如果有型別或格式驗證錯誤，就先回整頁或 fragment
		if (result.hasErrors()) {

			// 取第一個欄位錯誤訊息，顯示在 form 上方
			String errMsg = result.getFieldError().getDefaultMessage();
			model.addAttribute("errorMessage", errMsg);

			// 保留使用者原本輸入的值
			Map<String, String[]> criteria = request.getParameterMap();
			for (String key : criteria.keySet()) {
				model.addAttribute(key, criteria.get(key)[0]);
			}

			String xhr = request.getHeader("X-Requested-With");
			if ("XMLHttpRequest".equals(xhr)) {
				// Ajax 只回傳<table>那一塊（下方我們也會把 errorMessage 插進來）
				return "admin/fragments/room/listAllRoom :: roomResult";
			}

			// 回完整後台主頁，讓 form 上方的錯誤 alert 顯示
			model.addAttribute("mainFragment", "admin/fragments/room/listAllRoom");
			return "admin/index_admin";
		}
		// 正常走複合查詢
		// 2. 先把前端傳來的所有參數抓成 Map<String,String[]> (CriteriaHelper 用)
		Map<String, String[]> criteria = request.getParameterMap();

		// 3. 呼叫 Service 做搜尋
		List<RoomVO> roomList = roomSvc.compositeQuery(criteria);

		// 4. 回傳給前端用的 model 屬性
		// 查詢房間清單（多條件查詢）
		model.addAttribute("roomVOList", roomList);

		// 5. 為了讓查完之後，form 裡的欄位還能保留檢索值
		// 讓複合查詢欄位保持原值（用於 th:selected / th:value）
		for (String key : criteria.keySet()) {
			model.addAttribute(key, criteria.get(key)[0]);
		}

		// 6. 如果是 AJAX 請求，就只回傳 table fragment
		String xhr = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(xhr)) {
			// listAllRoom.html 裡面 <div th:fragment="roomResult"> 對應這個 fragment 名
			return "admin/fragments/room/listAllRoom :: roomResult";
		}

		// 7. 否則，就是首次或 user 直接瀏覽，回主畫面
		model.addAttribute("mainFragment", "admin/fragments/room/listAllRoom");
		return "admin/index_admin";

	}

	// 顯示新增 modal
	@GetMapping("/listAllRoom/add")
	public String showAddModal(Model model) {
		RoomVO roomVO = new RoomVO();
		// 預設房客名稱驗證格式
		roomVO.setRoomGuestName("");
		model.addAttribute("roomVO", roomVO);
		
		// 移除 BindingResult 避免影響 Thymeleaf
//	    model.asMap().remove("org.springframework.validation.BindingResult.roomVO");
		
		model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
		return "admin/fragments/room/modals/addRoom :: addRoomModalContent";
	}

	// 新增：帶上 Save 群組
	// 新增處理
	@PostMapping("/listAllRoom/insert")
	public String insertRoom(@Validated(RoomVO.Save.class) @ModelAttribute("roomVO") RoomVO roomVO,
			BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		if (result.hasErrors()) {
			model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
			model.addAttribute("roomVO", roomVO);
			return "admin/fragments/room/modals/addRoom :: addRoomModalContent";
		}

		try {
			roomSvc.addRoom(roomVO);
		} catch (IllegalArgumentException e) {
			// 加入錯誤訊息進 result
			result.rejectValue("roomId", null, e.getMessage());
			// 回前端顯示錯誤
			model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
			return "admin/fragments/room/modals/addRoom :: addRoomModalContent";
		}
		redirectAttributes.addFlashAttribute("message", "新增成功！");
		// 新增成功 → 回列表頁
		return "redirect:/admin/listAllRoom";
	}
	// 捕捉型別轉換錯誤（如roomId非數字）
//    @ExceptionHandler({org.springframework.beans.TypeMismatchException.class})
//    public String handleTypeMismatch(TypeMismatchException ex, Model model) {
//        RoomVO roomVO = new RoomVO();
//        model.addAttribute("roomVO", roomVO);
//        model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
//        model.addAttribute("typeError", "房間編號: 請輸入數字");
//        return "admin/fragments/room/modals/addRoom :: addRoomModalContent";
//    }

	// 顯示修改 modal
	@GetMapping("/listAllRoom/edit")
	public String showEditModal(@RequestParam("roomId") Integer roomId, Model model) {
		RoomVO roomVO = roomSvc.getOneRoom(roomId);
		model.addAttribute("roomVO", roomVO);
		model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
		return "admin/fragments/room/modals/update_room_input :: editRoomModalContent";
	}

	// 修改處理
	@PostMapping("/listAllRoom/update")
	public String updateRoom(@Validated(RoomVO.Save.class) @ModelAttribute("roomVO") RoomVO roomVO,
			BindingResult result, RedirectAttributes redirectAttributes, Model model) {
		// 型別轉換錯誤 & JSR303 驗證錯誤 都會進到這裡
		if (result.hasErrors()) {
			model.addAttribute("roomTypeVOList", roomTypeSvc.getAll());
			model.addAttribute("roomVO", roomVO);
			// 回傳fragment，讓JS replace Modal-body
			return "admin/fragments/room/modals/update_room_input :: editRoomModalContent";
		}
		roomSvc.updateRoom(roomVO);
		redirectAttributes.addFlashAttribute("message", "修改成功！");
		return "redirect:/admin/listAllRoom";
	}

	// 查看單筆資料 modal
//	@GetMapping("/listAllRoom/view")
//	public String viewRoom(@RequestParam("roomId") Integer roomId, Model model) {
//		RoomVO roomVO = roomSvc.getOneRoom(roomId);
//		model.addAttribute("roomVO", roomVO);
//		return "admin/fragments/room/modals/room_view :: viewModalContent";
//	}

	// 刪除
//    @GetMapping("/delete")
//    public String deleteRoom(@RequestParam("roomId") Integer roomId, RedirectAttributes redirectAttributes) {
//        roomSvc.deleteRoom(roomId);
//        redirectAttributes.addFlashAttribute("message", "刪除成功！");
//        return "redirect:/admin/room_info";
//    }

	// 房型列表供下拉選單用
	@ModelAttribute("roomTypeVOList")
	public List<RoomTypeVO> getRoomTypes() {
		return roomTypeSvc.getAll();
	}

}
