package com.roomtype.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;
import com.roomtypeschedule.model.RoomTypeScheduleVO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/")
public class FrontRoomTypeController {

	@Autowired
	RoomTypeService roomTypeSvc;
	
	@Autowired
	RoomTypeScheduleService roomTypeScheduleSvc;

	@ModelAttribute("roomTypeVOListData")
	protected List<RoomTypeVO> referenceListData() {
		List<RoomTypeVO> list = roomTypeSvc.getAll();
		return list;
	}
	
	@GetMapping("/roomtypes")
	public String showRoomTypeCards(
			HttpServletRequest request, HttpServletResponse response,
			 Model model) {
		List<RoomTypeVO> roomTypes = roomTypeSvc.getAllAvailableRoomTypes();
	    model.addAttribute("roomTypes", roomTypes);
	    return "front-end/room/roomCards";

	}
	
//	 ===== 顯示圖片 =====
	@GetMapping("/roomtypes/img/{id}")
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
	
	@GetMapping("/roomtype/{roomTypeId}/calendar")
	public String showRoomTypeCalendar(@PathVariable Integer roomTypeId, Model model) {
	    RoomTypeVO roomType = roomTypeSvc.getOneRoomType(roomTypeId);
	    if (roomType == null) {
	        return "redirect:/roomtypes/list"; // 或首頁
	    }
	    model.addAttribute("roomType", roomType);
	    return "front-end/room/roomTypeCalendar";
	}
	
	@GetMapping("/roomtype/{roomTypeId}/inventory-map")
	@ResponseBody
	public Map<String, Integer> getInventoryMap(@PathVariable Integer roomTypeId,
	                                            @RequestParam String start,
	                                            @RequestParam String end) {
	    RoomTypeVO roomTypeVO = roomTypeSvc.getOneRoomType(roomTypeId);

	    java.sql.Date startDate = java.sql.Date.valueOf(start);
	    java.sql.Date endDate = java.sql.Date.valueOf(end);

	    List<RoomTypeScheduleVO> schedules = roomTypeScheduleSvc.findSchedules(roomTypeVO, startDate, endDate);

	    return schedules.stream()
	            .collect(Collectors.toMap(
	                s -> s.getRoomOrderDate().toString(),
	                s -> s.getRoomAmount() - s.getRoomRSVBooked()
	            ));
	}
	
	@GetMapping("/roomtype/{roomTypeId}/inventory")
	@ResponseBody
	public Integer getRoomInventory(@PathVariable Integer roomTypeId, 
	                                @RequestParam String date) {
	    // 轉 java.sql.Date
	    java.sql.Date orderDate = java.sql.Date.valueOf(date);
	    RoomTypeVO roomType = roomTypeSvc.getOneRoomType(roomTypeId);
	    
	    // 找這天的預定資料
	    Optional<RoomTypeScheduleVO> scheduleOpt = 
	        roomTypeScheduleSvc.getByRoomTypeVOAndRoomOrderDate(roomType, orderDate);
	        
	    if (scheduleOpt.isPresent()) {
	        RoomTypeScheduleVO schedule = scheduleOpt.get();
	        return schedule.getRoomAmount() - schedule.getRoomRSVBooked();
	    }
	    return 0; // 沒資料就當作滿房
	}
	
	@GetMapping("/roomtype/{roomTypeId}/inventory-range")
	@ResponseBody
	public Integer getRoomInventoryRange(@PathVariable Integer roomTypeId,
	                                     @RequestParam String start,
	                                     @RequestParam String end) {
	    java.sql.Date startDate = java.sql.Date.valueOf(start);
	    java.sql.Date endDate = java.sql.Date.valueOf(end);

	    RoomTypeVO roomType = roomTypeSvc.getOneRoomType(roomTypeId);
	    List<RoomTypeScheduleVO> schedules = roomTypeScheduleSvc
	        .findSchedules(roomType, startDate, endDate);
	    
	    // 找區間內的最少可訂
	    int minRemaining = schedules.stream()
	        .mapToInt(s -> s.getRoomAmount() - s.getRoomRSVBooked())
	        .min()
	        .orElse(0);

	    return minRemaining;
	}
}
