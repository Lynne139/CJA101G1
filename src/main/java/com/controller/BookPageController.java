package com.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.roomOList.model.RoomOList;
import com.roomOrder.model.RoomOrder;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;

@Controller
public class BookPageController {
	
	@Autowired
	RoomTypeService roomTypeSvc;

	
	@PostMapping("/orderInfo")
	public String showOrderInfo(@RequestParam Map<String, String> params, Model model) {

		Map<String, RoomTypeVO> roomTypeVOMap = roomTypeSvc.getAllAvailableRoomTypes().stream()
			    .collect(Collectors.toMap(
			        roomType -> String.valueOf(roomType.getRoomTypeId()),
			        roomType -> roomType
			    ));
		
	    // 基本資料
	    String checkin = params.get("checkin");
	    String checkout = params.get("checkout");

	    long nights = 0;
	    try {
	        LocalDate start = LocalDate.parse(checkin);
	        LocalDate end = LocalDate.parse(checkout);
	        nights = ChronoUnit.DAYS.between(start, end);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    model.addAttribute("nights", nights);

	    // 房型對照表
	    Map<String, String> roomTypeNames = roomTypeSvc.getAllAvailableRoomTypes().stream()
	        .collect(Collectors.toMap(
	            roomType -> String.valueOf(roomType.getRoomTypeId()),
	            RoomTypeVO::getRoomTypeName
	        ));
	    Map<String, Integer> roomTypePrices = roomTypeSvc.getAllAvailableRoomTypes().stream()
	        .collect(Collectors.toMap(
	            roomType -> String.valueOf(roomType.getRoomTypeId()),
	            RoomTypeVO::getRoomTypePrice
	        ));
	    model.addAttribute("roomTypeNames", roomTypeNames);
	    model.addAttribute("roomTypePrices", roomTypePrices);

	    // 專案名稱寫死
	    Map<String, String> packageNames = new HashMap<>();
	    packageNames.put("800", "南島晨光專案");
	    packageNames.put("1800", "蔚藍晨夕專案");
	    packageNames.put("2800", "悠日饗茶專案");
	    model.addAttribute("packageNames", packageNames);

	    // 訂單物件
	    RoomOrder roomOrder = new RoomOrder();
	    roomOrder.setCheckInDate(checkin);
	    roomOrder.setCheckOutDate(checkout);

	    // 是否加購專案
	    if (params.get("package") != null && !params.get("package").equals("0")) {
	        roomOrder.setProjectAddOn(1);
	    } else {
	        roomOrder.setProjectAddOn(0);
	    }

	    // 房型明細
	    List<RoomOList> orderDetails = new ArrayList<>();
	    int totalRoomAmount = 0;

	    for (Map.Entry<String, String> entry : params.entrySet()) {
	        if (entry.getKey().startsWith("rooms_")) {
	            String roomTypeId = entry.getKey().substring(6);
	            String value = entry.getValue();
	            if (value != null && !value.isBlank()) {
	                int rooms = Integer.parseInt(value);
	                if (rooms > 0) {
	                    RoomOList detail = new RoomOList();
	                    RoomTypeVO vo = roomTypeVOMap.get(roomTypeId);
	                    detail.setRoomType(vo);
	                    detail.setRoomAmount(rooms);
	                    detail.setNumberOfPeople(Integer.parseInt(params.get("guests_" + roomTypeId)));
	                    detail.setRoomPrice(roomTypePrices.get(roomTypeId));
	                    totalRoomAmount += rooms * detail.getRoomPrice() * nights;
	                    orderDetails.add(detail);
	                }
	            }
	        }
	    }
	    
	    
	    Map<String, Integer> subtotalMap = new HashMap<>();
	    for (RoomOList detail : orderDetails) {
	        subtotalMap.put(
	            detail.getRoomType().getRoomTypeName(), 
	            detail.getRoomPrice() * (int)nights
	        );
	    }
	    model.addAttribute("subtotalMap", subtotalMap);

	    // 訂單放總金額
	    int guests = Integer.parseInt(params.get("guests"));
	    int packageSubtotal = 0;
	    if (params.get("package") != null && !params.get("package").equals("0")) {
	        int packagePrice = Integer.parseInt(params.get("package"));
	        packageSubtotal = packagePrice * guests * (int)nights;
	        model.addAttribute("packagePrice", packagePrice);
	    }
	    model.addAttribute("packageSubtotal", packageSubtotal);
	    roomOrder.setActualAmount(totalRoomAmount + packageSubtotal);
	    roomOrder.setOrderDetails(orderDetails);

	    // 模板顯示
	    model.addAttribute("roomOrder", roomOrder);
	    model.addAttribute("projectAddOnName", packageNames.get(params.get("package"))); // 不動 VO
	    model.addAttribute("params", params); // 如果頁面還在用 params
	    return "front-end/room/bookPage";
	}
	

}
