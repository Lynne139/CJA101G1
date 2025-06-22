package com.room.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.room.model.RoomService;
import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

@Controller
@Validated
@RequestMapping("/room")
public class RoomController {

	@Autowired
	RoomService roomSvc;

	@Autowired
	RoomTypeService roomTypeSvc;
	
	@GetMapping("addRoom")
	public String addRoom(ModelMap model) {
		RoomVO roomVO = new RoomVO();
		model.addAttribute("roomVO", roomVO);
		return "back-end/room/addRoom";
	}
	
	
	@PostMapping("insert")
	public String insert(@Valid RoomVO roomVO, BindingResult result, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		if (result.hasErrors()) {
		    return "back-end/room/addRoom";
		}
		/*************************** 2.開始新增資料 *****************************************/
		roomSvc.addRoom(roomVO);
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<RoomVO> list = roomSvc.getAll();
		model.addAttribute("roomListData", list); // for listAllRoom.html 第85行用
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/room/listAllRoom"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/room/listAllRoom")
	}	
	
	
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("roomId") String roomId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		RoomVO roomVO = roomSvc.getOneRoom(Integer.valueOf(roomId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("roomVO", roomVO);
		return "back-end/room/update_room_input"; // 查詢完成後轉交update_room_input.html
	}
	
	@PostMapping("update")
	public String update(@Valid RoomVO roomVO, BindingResult result, ModelMap model) {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		if (result.hasErrors()) {
		    return "back-end/room/update_room_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		roomSvc.updateRoom(roomVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		roomVO = roomSvc.getOneRoom(Integer.valueOf(roomVO.getRoomId()));
		model.addAttribute("roomVO", roomVO);
		return "back-end/room/listOneRoom"; // 修改成功後轉交listOneRoom.html
	}
	
	@ModelAttribute("roomTypeListData")
	protected List<RoomTypeVO> referenceListData() {
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		return list;
	}
	
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
		@RequestParam("roomId") String roomId,
		ModelMap model) {
		
		/***************************2.開始查詢資料*********************************************/
		RoomVO roomVO = roomSvc.getOneRoom(Integer.valueOf(roomId));
		
		List<RoomVO> list = roomSvc.getAll();
		model.addAttribute("roomTypeListData", list);     // for select_page.html 第97 109行用
		model.addAttribute("roomTypeVO", new RoomTypeVO());  // for select_page.html 第133行用
		List<RoomTypeVO> list2 = roomTypeSvc.getAll();
    	model.addAttribute("roomTypeListData",list2);    // for select_page.html 第135行用
		
		if (roomVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/room/select_page";
		}
		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("roomVO", roomVO); // for1 --> listOneEmp.html 的第37~44行用
                                            // for2 --> select_page.html的第156用
//		return "back-end/room/listOneRoom";   // 查詢完成後轉交listOneEmp.html
		return "back-end/room/select_page";  // 查詢完成後轉交select_page.html由其第158行insert listOneEmp.html內的th:fragment="listOneEmp-div
	}
	
}
