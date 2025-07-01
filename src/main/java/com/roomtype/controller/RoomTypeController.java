package com.roomtype.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.room.model.RoomService;
import com.room.model.RoomVO;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.OptimisticLockException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@Validated
@RequestMapping("/admin")
public class RoomTypeController {
	@Autowired
	RoomService roomSvc;

	@Autowired
	RoomTypeService roomTypeSvc;

	@ModelAttribute("roomTypeVOListData")
	protected List<RoomTypeVO> referenceListData() {
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		return list;
	}

	@GetMapping("/listAllRoomType")
	public String listAllRoomType(HttpServletRequest request, HttpServletResponse response,
			@Valid @ModelAttribute("roomTypeVO") RoomTypeVO roomTypeVO, BindingResult result, Model model) {
		// 1. sidebar
		model.addAttribute("currentURI", request.getRequestURI());

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
				return "admin/fragments/room/listAllRoomType :: roomTypeResult";
			}

			// 回完整後台主頁，讓 form 上方的錯誤 alert 顯示
			model.addAttribute("mainFragment", "admin/fragments/room/listAllRoomType");
			return "admin/index_admin";
		}
		// 正常走複合查詢
		// 2. 先把前端傳來的所有參數抓成 Map<String,String[]> (CriteriaHelper 用)
		Map<String, String[]> criteria = request.getParameterMap();

		// 3. 呼叫 Service 做搜尋
		List<RoomTypeVO> roomTypeList = roomTypeSvc.compositeQuery(criteria);

		// 4. 回傳給前端用的 model 屬性
		// 查詢房間清單（多條件查詢）
		model.addAttribute("roomTypeVOList", roomTypeList);

		// 5. 為了讓查完之後，form 裡的欄位還能保留檢索值
		// 讓複合查詢欄位保持原值（用於 th:selected / th:value）
		for (String key : criteria.keySet()) {
			model.addAttribute(key, criteria.get(key)[0]);
		}

		// 6. 如果是 AJAX 請求，就只回傳 table fragment
		String xhr = request.getHeader("X-Requested-With");
		if ("XMLHttpRequest".equals(xhr)) {
			// listAllRoom.html 裡面 <div th:fragment="roomResult"> 對應這個 fragment 名
			return "admin/fragments/room/listAllRoomType :: roomTypeResult";
		}

		// 7. 否則，就是首次或 user 直接瀏覽，回主畫面
		model.addAttribute("mainFragment", "admin/fragments/room/listAllRoomType");
		return "admin/index_admin";

	}

	// ===== 細項檢視 =====
	@GetMapping("/listAllRoomType/view")
	public String showViewModal(@ModelAttribute("roomTypeVO") RoomTypeVO roomTypeVO, Model model) {

		RoomTypeVO roomType = roomTypeSvc.getRoomTypeAmountForOne(roomTypeVO);
		model.addAttribute("roomTypeVO", roomType);
		return "admin/fragments/room/modals/getOneRoomType :: viewRoomTypeModalContent";
	}

//	 ===== 顯示圖片 =====
	@GetMapping("/listAllRoomType/img/{id}")
	public ResponseEntity<byte[]> getImage(@PathVariable("id") Integer roomTypeId) {
		System.out.println("🔍 查詢圖片的房型ID：" + roomTypeId);
		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(roomTypeId);
		byte[] imageBytes = roomTypeVO.getRoomTypePic();
		System.out.println("🖼 圖片 byte 數量：" + (imageBytes != null ? imageBytes.length : "null"));
		if (imageBytes == null || imageBytes.length == 0) {
			// 回傳no_img.svg bytes
			try (InputStream is = getClass().getResourceAsStream("/static/images/admin/no_img.svg")) {
				if (is != null) {
					byte[] defaultImg = is.readAllBytes();
					return ResponseEntity.ok().header(HttpHeaders.CONTENT_TYPE, "image/svg+xml").body(defaultImg);
				}
			} catch (IOException e) {
				// optional: log
			}
			return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
		}

		// 自動判斷圖片格式
		String contentType = detectImageMimeType(imageBytes);
		if (contentType == null) {
			contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // fallback
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType(contentType));
		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

	private String detectImageMimeType(byte[] imageBytes) {
		try (InputStream is = new ByteArrayInputStream(imageBytes)) {
			ImageInputStream iis = ImageIO.createImageInputStream(is);
			Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
			if (readers.hasNext()) {
				ImageReader reader = readers.next();
				String formatName = reader.getFormatName().toLowerCase();
				switch (formatName) {
				case "jpeg":
					return "image/jpeg";
				case "png":
					return "image/png";
				case "gif":
					return "image/gif";
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	// ====新增====
	// 第一步，畫面顯示填寫表單
	@GetMapping("/listAllRoomType/addRoomType") // 瀏覽器的get請求
	public String showAddModal(ModelMap model) {
		RoomTypeVO roomTypeVO = new RoomTypeVO();// 建立空的roomTypeVO
		model.addAttribute("roomTypeVO", roomTypeVO);// 讓 Thymeleaf 可以綁定資料
		return "admin/fragments/room/modals/addRoomType :: addRoomTypeModalContent";
	}

	// 第二部新增到資料庫
	@PostMapping("/listAllRoomType/insert") // 表單送出請求
	public String insertRoomType(
			@Validated(RoomTypeVO.Save.class) @ModelAttribute("roomTypeVO") RoomTypeVO roomTypeVO, // 自動把表單欄位填入																										// roomTypeVO，並進行格式驗證
			BindingResult result, // 儲存驗證錯誤結果
			@RequestParam(value = "uploadImg", required = false) MultipartFile imageFile,
			@RequestParam(value = "clearImgFlag", required = false) String clearImgFlag,
			RedirectAttributes redirectAttributes, ModelMap model// 把「後端資料」傳到「畫面」的工具
	) {

		// 錯誤 flag（初始 false）
		boolean hasImageError = false;

		// 檢查圖片格式與大小
		if (imageFile != null && !imageFile.isEmpty()) {
			String contentType = imageFile.getContentType();
			long maxSize = 16 * 1024 * 1024; // 16MB

			if (!isValidImageType(contentType)) {
				model.addAttribute("imageError", "只接受 PNG / JPEG / GIF 格式圖片");
				hasImageError = true;
			} else if (imageFile.getSize() > maxSize) {
				model.addAttribute("imageError", "圖片大小不得超過 16MB");
				hasImageError = true;
			} else {
				try {
					roomTypeVO.setRoomTypePic(imageFile.getBytes());
				} catch (IOException e) {
					model.addAttribute("imageError", "圖片處理失敗");
					hasImageError = true;
				}
			}
		}

		// 驗證名稱重複
		if (roomTypeSvc.existsDuplicateName(roomTypeVO.getRoomTypeName())) {
			result.rejectValue("roomTypeName", null, "房型名稱已存在，請重新輸入！");
			hasImageError = true;
		}

		// 若欄位驗證有錯，或圖片錯誤，回填 modal
		if (result.hasErrors() || hasImageError) {
			// 避免input有新選其他圖，但表單驗證被擋時，回填的model記成input失敗的內容導致preview錯亂
			roomTypeVO.setRoomTypePic(null);
			model.addAttribute("roomTypeVO", roomTypeVO);
			return "admin/fragments/room/modals/addRoomType :: addRoomTypeModalContent";
		}

		// 寫入資料庫
		roomTypeSvc.saveWithImage(roomTypeVO, imageFile, clearImgFlag);
		redirectAttributes.addFlashAttribute("message", "新增成功！");
		return "redirect:/admin/listAllRoomType";
	}

	// 支援格式判斷
	private boolean isValidImageType(String contentType) {
		return contentType != null && (contentType.equalsIgnoreCase("image/png")
				|| contentType.equalsIgnoreCase("image/jpeg") || contentType.equalsIgnoreCase("image/gif"));
	}

	// ====修改====
	// 第一步進入修改頁面
	@GetMapping("/listAllRoomType/edit")
	public String showEditModal(@RequestParam("roomTypeId") Integer roomTypeId, Model model) {
		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(roomTypeId);

		model.addAttribute("roomTypeVO", roomTypeVO);
		return "admin/fragments/room/modals/update_roomType_input :: editRoomTypeModalContent"; // 查詢完成後轉交update_roomType_input.html
	}

	// 第二步將修改資料送進資料庫
	@PostMapping("/listAllRoomType/update")
	public String updateRoomType(@Validated(RoomTypeVO.Save.class) @ModelAttribute("roomTypeVO") RoomTypeVO roomTypeVO, BindingResult result,
			@RequestParam(value = "uploadImg", required = false) MultipartFile imageFile,
			@RequestParam(value = "clearImgFlag", required = false) String clearImgFlag,
			RedirectAttributes redirectAttributes, Model model) {

		// 錯誤 flag（初始 false）
	    boolean hasImageError = false;

	    // 處理圖片格式與大小
	    if (imageFile != null && !imageFile.isEmpty()) {
	        String contentType = imageFile.getContentType();
	        long maxSize = 16 * 1024 * 1024;

	        if (!isValidImageType(contentType)) {
	            model.addAttribute("imageError", "只接受 PNG / JPEG / GIF 格式圖片");
	            hasImageError = true;
	        } else if (imageFile.getSize() > maxSize) {
	            model.addAttribute("imageError", "圖片大小不得超過 16MB");
	            hasImageError = true;
	        } else {
	            try {
	            	roomTypeVO.setRoomTypePic(imageFile.getBytes());
	            } catch (IOException e) {
	                model.addAttribute("imageError", "圖片處理失敗");
	                hasImageError = true;
	            }
	        }
	    }

	    // 驗證名稱重複
	    if (roomTypeSvc.isDuplicateName(roomTypeVO.getRoomTypeName(),roomTypeVO.getRoomTypeId())) {
	        result.rejectValue("roomTypeName", null, "房型名稱已存在，請重新輸入！");
	    }

	    // 若欄位驗證有錯，或圖片錯誤，回填 modal
	    if (result.hasErrors() || hasImageError) {
	    	// 把資料庫圖片補回去(避免input有新選其他圖，但表單驗證被擋時，回填的model記成input失敗的內容導致preview錯亂)
	        byte[] originalImg = roomTypeSvc.getOneRoomType(roomTypeVO.getRoomTypeId()).getRoomTypePic();
	        roomTypeVO.setRoomTypePic(originalImg);

	        model.addAttribute("roomTypeVO", roomTypeVO);
	        return "admin/fragments/room/modals/update_roomType_input :: editRoomTypeModalContent";
	    }
	    
	    //送出時以version判斷是否期間有人做更動避免覆蓋更新
//	    try {
	    	roomTypeSvc.saveWithImage(roomTypeVO, imageFile, clearImgFlag);
//	    } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
//	        model.addAttribute("errorMsg", e.getMessage());
//	        model.addAttribute("resto", resto); // 回填原輸入
//	        return "admin/fragments/resto/modals/resto_edit :: editModalContent";
//	    }
	    redirectAttributes.addFlashAttribute("message", "編輯成功！");
	    return "redirect:/admin/listAllRoomType";
	}

//	@PostMapping("getOne_For_Display")
//	public String getOne_For_Display(
//		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
//		@RequestParam("roomTypeId") String roomTypeId,
//		ModelMap model) {
//		
//		/***************************2.開始查詢資料*********************************************/
//		RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(Integer.valueOf(roomTypeId));
//		
//		List<RoomTypeVO> list = roomTypeSvc.getAll();
//		model.addAttribute("roomTypeVOList", list);     // for select_page.html 第97 109行用
//		model.addAttribute("roomTypeVO", new RoomTypeVO());  // for select_page.html 第133行用
//		List<RoomTypeVO> list2 = roomTypeSvc.getAll();
//    	model.addAttribute("roomTypeVOList",list2);    // for select_page.html 第135行用
//		
//		if (roomTypeVO == null) {
//			model.addAttribute("errorMessage", "查無資料");
//			return "back-end/room/select_page";
//		}
//		
//		/***************************3.查詢完成,準備轉交(Send the Success view)*****************/
//		model.addAttribute("roomTypeVO", roomTypeVO); // for1 --> listOneEmp.html 的第37~44行用
	// for2 --> select_page.html的第156用
//		return "back-end/room/listOneRoom";   // 查詢完成後轉交listOneEmp.html
//		return "back-end/room/select_page";  // 查詢完成後轉交select_page.html由其第158行insert listOneEmp.html內的th:fragment="listOneEmp-div
//	}

}
