package com.roomtype.controller;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class RoomTypeController {
	@Autowired
	RoomTypeService roomTypeSvc;
	
	@ModelAttribute("roomTypeListData")
	protected List<RoomTypeVO> referenceListData() {
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		return list;
	}
	
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
		@RequestParam("roomTypeId") String roomTypeId,
		ModelMap model) {
		
		/***************************2.開始查詢資料*********************************************/
		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(Integer.valueOf(roomTypeId));
		
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		model.addAttribute("roomTypeListData", list);     // for select_page.html 第97 109行用
		model.addAttribute("roomTypeVO", new RoomTypeVO());  // for select_page.html 第133行用
		List<RoomTypeVO> list2 = roomTypeSvc.getAll();
    	model.addAttribute("roomTypeListData",list2);    // for select_page.html 第135行用
		
		if (roomTypeVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/room/select_page";
		}
		
		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
		model.addAttribute("roomTypeVO", roomTypeVO); // for1 --> listOneEmp.html 的第37~44行用
                                            // for2 --> select_page.html的第156用
//		return "back-end/room/listOneRoom";   // 查詢完成後轉交listOneEmp.html
		return "back-end/room/select_page";  // 查詢完成後轉交select_page.html由其第158行insert listOneEmp.html內的th:fragment="listOneEmp-div
	}
	
	//====新增====
	//第一步，畫面顯示填寫表單
	@GetMapping("/listAllRoomType/addRoomType")//瀏覽器的get請求
	public String addRoomType(ModelMap model) {
		RoomTypeVO roomTypeVO = new RoomTypeVO();//建立空的roomTypeVO
		model.addAttribute("roomTypeVO", roomTypeVO);//讓 Thymeleaf 可以綁定資料
		return "admin/fragments/room/models/addRoomType :: addRoomTypeModelContent";
	}
	//第二部新增到資料庫
	@PostMapping("/roomType_info/insert")//表單送出請求
	public String insert(
			@Valid RoomTypeVO roomTypeVO,//自動把表單欄位填入 roomTypeVO，並進行格式驗證
			BindingResult result,//儲存驗證錯誤結果
			ModelMap model,//把「後端資料」傳到「畫面」的工具
			@RequestParam("roomTypePic") MultipartFile[] parts) throws IOException{//接收多個上傳的圖片

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(roomTypeVO, result, "roomTypePic");//把驗證失敗時多餘的錯誤（比如圖片沒上傳的）移除。

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "員工照片: 請上傳照片");
		} else {//把圖片轉成 byte[] 存到 roomTypeVO 的圖片欄位中
			for (MultipartFile multipartFile : parts) {
				byte[] buf;
				
					buf = multipartFile.getBytes();
				
				roomTypeVO.setRoomTypePic(buf);
			}
		}
		if (result.hasErrors() || parts[0].isEmpty()) {//如果驗證錯誤就回到原表單畫面
			return "back-end/roomType/addRoomType";
		}
		/*************************** 2.開始新增資料 *****************************************/
		roomTypeSvc.addRoomType(roomTypeVO);//呼叫 Service 儲存資料到資料庫
		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		model.addAttribute("roomTypeListData", list); // for listAllRoomType.html 第85行用
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/roomType/listAllRoomType"; // 新增成功後重導至IndexController_inSpringBoot.java的第58行@GetMapping("/roomType/listAllRoomType")
	}
	
	//====修改====
	//第一步進入修改頁面
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("roomTypeId") String roomTypeId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(Integer.valueOf(roomTypeId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("roomTypeVO", roomTypeVO);
		return "back-end/roomType/update_roomType_input"; // 查詢完成後轉交update_roomType_input.html
	}
	
	//第二步將修改資料送進資料庫
	@PostMapping("update")
	public String update(@Valid RoomTypeVO roomTypeVO, BindingResult result, ModelMap model,
			@RequestParam("roomTypePic") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 去除BindingResult中upFiles欄位的FieldError紀錄 --> 見第172行
		result = removeFieldError(roomTypeVO, result, "roomTypePic");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的新圖片時
			byte[] roomTypePic = roomTypeSvc.getOneRoomType(roomTypeVO.getRoomTypeId()).getRoomTypePic();
			roomTypeVO.setRoomTypePic(roomTypePic);
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] roomTypePic = multipartFile.getBytes();
				roomTypeVO.setRoomTypePic(roomTypePic);
			}
		}
		if (result.hasErrors()) {
			return "back-end/roomType/update_roomType_input";
		}
		/*************************** 2.開始修改資料 *****************************************/
		roomTypeSvc.updateRoomType(roomTypeVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		roomTypeVO = roomTypeSvc.getOneRoomType(Integer.valueOf(roomTypeVO.getRoomTypeId()));
		model.addAttribute("roomTypeVO", roomTypeVO);
		return "back-end/roomType/listOneRoomType"; // 修改成功後轉交listOneRoomType.html
	}
	
	public BindingResult removeFieldError(RoomTypeVO roomTypeVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldname -> !fieldname.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		result = new BeanPropertyBindingResult(roomTypeVO, "roomTypeVO");
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

}
