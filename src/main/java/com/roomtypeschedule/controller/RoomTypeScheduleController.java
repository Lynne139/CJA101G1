package com.roomtypeschedule.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;
import com.roomtypeschedule.model.RoomTypeScheduleVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@Validated
@RequestMapping("/admin")
public class RoomTypeScheduleController {

	@Autowired
	RoomTypeScheduleService roomTypeScheduleSvc;

	@Autowired
	RoomTypeService roomTypeSvc;
	
	@GetMapping("/listAllRoomTypeSchedule")
	public String listAllRoomTypeSchedule(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("roomTypeScheduleVO") RoomTypeScheduleVO roomTypeScheduleVO, BindingResult result, Model model) {
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
				return "admin/fragments/room/listAllRoomTypeSchedule :: roomTypeScheduleResult";
			}

			// 回完整後台主頁，讓 form 上方的錯誤 alert 顯示
			model.addAttribute("mainFragment", "admin/fragments/room/listAllRoomTypeSchedule");
			return "admin/index_admin";
		}
		// 正常走複合查詢
		// 2. 先把前端傳來的所有參數抓成 Map<String,String[]> (CriteriaHelper 用)
		Map<String, String[]> criteria = request.getParameterMap();

		// 3. 呼叫 Service 做搜尋
		List<RoomTypeScheduleVO> roomTypeScheduleVOList = roomTypeScheduleSvc.compositeQuery(criteria);

		// 4. 回傳給前端用的 model 屬性
		// 查詢房間清單（多條件查詢）
		model.addAttribute("roomTypeScheduleVOList", roomTypeScheduleVOList);

		// 5. 為了讓查完之後，form 裡的欄位還能保留檢索值
		// 讓複合查詢欄位保持原值（用於 th:selected / th:value）
		for (String key : criteria.keySet()) {
			model.addAttribute(key, criteria.get(key)[0]);
		}

		// 6. 如果是 AJAX 請求，就只回傳 table fragment
		String xhr = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(xhr)) {
			// listAllRoom.html 裡面 <div th:fragment="roomResult"> 對應這個 fragment 名
			return "admin/fragments/room/listAllRoomTypeSchedule :: roomTypeScheduleResult";
		}

		// 7. 否則，就是首次或 user 直接瀏覽，回主畫面
		model.addAttribute("mainFragment", "admin/fragments/room/listAllRoomTypeSchedule");
		return "admin/index_admin";

	}
	
//	@GetMapping("/roomTypeSchedule/init")
//	public String initializeRoomTypeSchedules(@RequestParam Integer roomTypeId, Model model) {
//	    RoomTypeVO roomType = roomTypeSvc.getOneRoomType(roomTypeId);
//
//	    LocalDate startDate = LocalDate.now();
//	    LocalDate endDate = startDate.plusMonths(6);
//
//	    roomTypeScheduleSvc.initializeOrUpdateRoomTypeSchedule(roomType, startDate, endDate);
//
//	    model.addAttribute("message", "成功初始化未來半年資料");
//	    return "redirect:/admin/roomType/list";
//	}
	
	@GetMapping("/listAllRoomTypeSchedule/initAll")
	@ResponseBody
	public String initializeAllRoomTypeSchedules(RedirectAttributes ra, Model model) {
	    List<RoomTypeVO> roomTypes = roomTypeSvc.getAll();

	    LocalDate startDate = LocalDate.now();
	    LocalDate endDate = startDate.plusMonths(1);

	    for (RoomTypeVO roomType : roomTypes) {
	        roomTypeScheduleSvc.initializeOrUpdateRoomTypeSchedule(roomType, startDate, endDate);
	    }

	    ra.addFlashAttribute("success", "已為所有房型批次產生未來 1 個月排程！");
	    model.addAttribute("mainFragment", "admin/fragments/room/listAllRoomTypeSchedule");
		return "admin/index_admin";
//	    return "redirect:/admin/roomType/list";
	}
}
