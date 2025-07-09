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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import com.coupon.service.MemberCouponService;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.notification.service.NotificationService;
import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class BookPageController {

	@Autowired
	RoomTypeService roomTypeSvc;

	@Autowired
	private MemberService memberService;

	@Autowired
	private MemberCouponService memberCouponService;

	@Autowired
	private RoomOrderService roomOrderService;

	@Autowired
	private CouponService couponService;

	@Autowired
	private RoomOListService roomOListService;

	@Autowired
	private RoomTypeService roomTypeService;

	@Autowired
	private RoomTypeScheduleService roomScheduleService;

	@Autowired
	private NotificationService notificationService;

	@PostMapping("/orderInfo")
	public String showOrderInfo(@RequestParam Map<String, String> params,
			Model model,
			HttpSession session) {
		MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
		model.addAttribute("member", member);
		System.out.println(member.getMemberId());

		Map<String, RoomTypeVO> roomTypeVOMap = roomTypeSvc.getAllAvailableRoomTypes().stream()
				.collect(Collectors.toMap(
						roomType -> String.valueOf(roomType.getRoomTypeId()),
						roomType -> roomType));

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
						RoomTypeVO::getRoomTypeName));
		Map<String, Integer> roomTypePrices = roomTypeSvc.getAllAvailableRoomTypes().stream()
				.collect(Collectors.toMap(
						roomType -> String.valueOf(roomType.getRoomTypeId()),
						RoomTypeVO::getRoomTypePrice));
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
					detail.getRoomPrice() * (int) nights);
		}
		model.addAttribute("subtotalMap", subtotalMap);

		// 訂單放總金額
		int guests = Integer.parseInt(params.get("guests"));
		int packageSubtotal = 0;
		if (params.get("package") != null && !params.get("package").equals("0")) {
			int packagePrice = Integer.parseInt(params.get("package"));
			packageSubtotal = packagePrice * guests * (int) nights;
			model.addAttribute("packagePrice", packagePrice);
		}
		model.addAttribute("packageSubtotal", packageSubtotal);
		roomOrder.setTotalAmount(totalRoomAmount + packageSubtotal);
		roomOrder.setActualAmount(totalRoomAmount + packageSubtotal);
		roomOrder.setOrderDetails(orderDetails);

		// 模板顯示
		model.addAttribute("roomOrder", roomOrder);
		model.addAttribute("projectAddOnName", packageNames.get(params.get("package")));
		model.addAttribute("params", params);
		return "front-end/room/bookPage";
	}

	// ===== 查詢會員擁有的所有適用優惠券列表 =====
	@GetMapping("/orderInfo/member_coupons")
	@ResponseBody
	public List<Coupon> getMemberCoupons(
			@RequestParam("memberId") Integer memberId,
			@RequestParam("totalAmount") Integer priceBeforeUsingCoupon) {
		return memberCouponService.getRoomOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
	}

	// =====統一處理表單提交=====

	@PostMapping("/orderInfo/confirm")
	@ResponseBody
	public String processBookingForm(@ModelAttribute RoomOrder roomOrder,
			HttpSession session, Model model, HttpServletRequest request) {

		// 設定會員
		MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
		if (member == null) {
			model.addAttribute("errorMessage", "請先登入會員");
			return "front-end/member/login";
		}
		roomOrder.setMember(member);

		// 驗證碼檢查
		String captcha = request.getParameter("captcha");
		if (captcha == null || captcha.trim().isEmpty()) {
			model.addAttribute("errorMessage", "請輸入驗證碼");
			return prepareBookPageData(model, roomOrder);
		}

		// 根據付款方式決定處理流程
		String payMethod = roomOrder.getPayMethod();
		if ("0".equals(payMethod)) {
			// 臨櫃付款 - 直接建立訂單並跳轉到確認頁面
			return processCounterPayment(roomOrder, session, model, request);
		} else if ("1".equals(payMethod)) {
			// LINE Pay - 暫存資料到 Session，返回成功狀態讓前端調用 LINE Pay API
			// 這裡直接回傳 JSON
			Map<String, Object> result = processLinePayPreparation(roomOrder, session, model, request);
			// 轉成 JSON 字串回傳
			try {
				return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(result);
			} catch (Exception e) {
				return "{\"status\":\"error\",\"message\":\"JSON 轉換失敗\"}";
			}
		} else {
			model.addAttribute("errorMsg", "請選擇付款方式");
			return prepareBookPageData(model, roomOrder);
		}
	}

	// 修改 processLinePayPreparation，直接回傳 JSON 狀態
	@ResponseBody
	private Map<String, Object> processLinePayPreparation(RoomOrder roomOrder, HttpSession session,
			Model model, HttpServletRequest request) {
		Map<String, Object> result = new HashMap<>();
		try {
			// Set order basic data
			roomOrder.setOrderStatus(0); // Pending payment
			roomOrder.setOrderDate(LocalDate.now().toString());
			roomOrder.setPayStatus("0"); // Unpaid

			// Process coupon
			processCoupon(roomOrder, request);

			// Parse order details
			List<RoomOList> details = parseOrderDetailsFromRequest(request, roomOrder);
			roomOrder.setOrderDetails(details);

			// Store order data in session
			session.setAttribute("pendingRoomOrder", roomOrder);

			// 回傳成功狀態
			result.put("status", "success");
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			result.put("status", "error");
			result.put("message", "LINE Pay 準備失敗：" + e.getMessage());
			return result;
		}
	}

	// Add error handling to counter payment processing
	private String processCounterPayment(RoomOrder roomOrder, HttpSession session,
			Model model, HttpServletRequest request) {
		try {
			// Set order basic data
			roomOrder.setOrderStatus(1); // Confirmed
			roomOrder.setOrderDate(LocalDate.now().toString());
			roomOrder.setPayStatus("0"); // Unpaid

			// Process coupon
			processCoupon(roomOrder, request);

			// Save main order
			RoomOrder savedOrder = roomOrderService.save(roomOrder);
			if (savedOrder == null) {
				throw new RuntimeException("訂單儲存失敗");
			}

			// Parse and save order details
			List<RoomOList> details = parseOrderDetailsFromRequest(request, savedOrder);
			for (RoomOList detail : details) {
				RoomOList savedDetail = roomOListService.save(detail);
				if (savedDetail == null) {
					throw new RuntimeException("訂單明細儲存失敗");
				}
			}

			// Update inventory
			for (RoomOList detail : details) {
				roomScheduleService.reserve(detail);
			}

			// Update member data
			memberService.updateConsumptionAndLevelAndPoints(
					roomOrder.getMember().getMemberId(), savedOrder.getActualAmount());

			// Send notification
			notificationService.createNotification(
					roomOrder.getMember().getMemberId(), "訂單成立", "訂單已成立，請至訂單查詢頁面查看");

			// Process coupon usage
			String couponCode = request.getParameter("couponCode");
			if (couponCode != null && !couponCode.trim().isEmpty()) {
				memberCouponService.useCoupon(roomOrder.getMember().getMemberId(), couponCode);
			}

			// Put order data in session and redirect to confirmation page
			session.setAttribute("confirmedOrder", savedOrder);
			return "redirect:/orderInfo/orderConfirm";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "訂單建立失敗：" + e.getMessage());
			return prepareBookPageData(model, roomOrder);
		}
	}

	// ===== LINE Pay 付款成功後的處理 =====
	@GetMapping("/orderInfo/linepay-success")
	public String handleLinePaySuccess(@RequestParam String orderId,
			HttpSession session, Model model) {

		try {
			// 從 Session 取得待付款訂單
			RoomOrder roomOrder = (RoomOrder) session.getAttribute("pendingRoomOrder");
			if (roomOrder == null) {
				model.addAttribute("errorMsg", "查無待付款訂單資料");
				return "front-end/room/bookPage";
			}

			// 更新訂單狀態
			roomOrder.setOrderStatus(1); // 已確認
			roomOrder.setPayStatus("1"); // 已付款

			// 儲存主訂單
			RoomOrder savedOrder = roomOrderService.save(roomOrder);

			// 儲存訂單明細
			List<RoomOList> details = roomOrder.getOrderDetails();
			for (RoomOList detail : details) {
				detail.setRoomOrder(savedOrder);
				roomOListService.save(detail);
			}

			// 更新庫存
			for (RoomOList detail : details) {
				roomScheduleService.reserve(detail);
			}

			// 更新會員資料
			memberService.updateConsumptionAndLevelAndPoints(
					roomOrder.getMember().getMemberId(), savedOrder.getActualAmount());

			// 發通知
			notificationService.createNotification(
					roomOrder.getMember().getMemberId(), "訂單成立", "LINE Pay 付款成功，訂單已成立");

			// 處理折價券使用
			if (roomOrder.getCoupon() != null) {
				memberCouponService.useCoupon(
						roomOrder.getMember().getMemberId(),
						roomOrder.getCoupon().getCouponCode());
			}

			// 清除 Session 中的待付款訂單
			session.removeAttribute("pendingRoomOrder");

			// 將完成的訂單放入 Session，轉跳至確認頁面
			session.setAttribute("confirmedOrder", savedOrder);
			return "redirect:/orderInfo/orderConfirm";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "訂單處理失敗：" + e.getMessage());
			return "front-end/room/bookPage";
		}
	}

	// ===== 折價券處理共用方法 =====
	private void processCoupon(RoomOrder roomOrder, HttpServletRequest request) {
		String couponCode = request.getParameter("couponCode");
		if (couponCode != null && !couponCode.trim().isEmpty()) {
			Coupon coupon = couponService.getCouponByCode(couponCode).orElse(null);
			if (coupon != null) {
				roomOrder.setCoupon(coupon);
				// 這裡假設前端已經計算好折扣後的金額
				// 如果需要在後端重新計算，可以在這裡處理
			}
		}
	}

	// ===== 訂單確認頁面 =====
	// Add this method to handle the chunked encoding issue
	@GetMapping("/orderInfo/orderConfirm")
	public String showConfirmPage(HttpSession session, Model model) {
		RoomOrder confirmedOrder = (RoomOrder) session.getAttribute("confirmedOrder");
		if (confirmedOrder == null) {
			// Better error handling
			model.addAttribute("errorMsg", "查無訂單資料，請重新下單。");
			return "front-end/room/bookPage";
		}

		try {
			// Ensure all required data is present
			if (confirmedOrder.getOrderDetails() != null) {
				for (RoomOList detail : confirmedOrder.getOrderDetails()) {
					if (detail.getRoomType() == null && detail.getRoomTypeId() != null) {
						RoomTypeVO roomType = roomTypeSvc.getOneRoomType(detail.getRoomTypeId());
						if (roomType != null) {
							detail.setRoomType(roomType);
						}
					}
				}
			}

			// Calculate nights safely
			long nights = 0;
			if (confirmedOrder.getCheckInDate() != null && confirmedOrder.getCheckOutDate() != null) {
				LocalDate checkIn = LocalDate.parse(confirmedOrder.getCheckInDate());
				LocalDate checkOut = LocalDate.parse(confirmedOrder.getCheckOutDate());
				nights = ChronoUnit.DAYS.between(checkIn, checkOut);
			}

			// Set default values to prevent null references
			model.addAttribute("roomOrder", confirmedOrder);
			model.addAttribute("nights", nights);

			// Add project add-on name if exists
			if (confirmedOrder.getProjectAddOn() == 1) {
				Map<String, String> packageNames = new HashMap<>();
				packageNames.put("800", "南島晨光專案");
				packageNames.put("1800", "蔚藍晨夕專案");
				packageNames.put("2800", "悠日饗茶專案");
				// You'll need to determine which package was selected
				model.addAttribute("projectAddOnName", "專案加購"); // Default value
				model.addAttribute("packagePrice", 0); // Default value
			}

			// Set default discount amount if not set
			if (confirmedOrder.getDiscountAmount() == null) {
				confirmedOrder.setDiscountAmount(0);
			}

			// Clear session after setting all data
			// session.removeAttribute("confirmedOrder"); // 這行先註解掉

			return "front-end/room/orderConfirm";

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("errorMsg", "訂單資料處理失敗：" + e.getMessage());
			return "redirect:/member/center";
		}
	}

	// ===== 解析訂單明細 =====
	private List<RoomOList> parseOrderDetailsFromRequest(HttpServletRequest request, RoomOrder savedOrder) {
		List<RoomOList> details = new ArrayList<>();
		int i = 0;

		while (request.getParameter("orderDetails[" + i + "].roomTypeId") != null) {
			RoomOList detail = new RoomOList();

			// 設定基本資料
			detail.setListStatus(1); // 預設狀態

			// 取得房型ID
			String roomTypeIdStr = request.getParameter("orderDetails[" + i + "].roomTypeId");
			if (roomTypeIdStr != null && !roomTypeIdStr.isEmpty()) {
				Integer roomTypeId = Integer.valueOf(roomTypeIdStr);
				detail.setRoomTypeId(roomTypeId);

				// 設定房型資料
				RoomTypeVO roomType = roomTypeService.getOneRoomType(roomTypeId);
				detail.setRoomType(roomType);
			}

			// 取得房間數量
			String roomAmountStr = request.getParameter("orderDetails[" + i + "].roomAmount");
			detail.setRoomAmount(
					(roomAmountStr != null && !roomAmountStr.isEmpty()) ? Integer.valueOf(roomAmountStr) : 0);

			// 取得房間價格
			String roomPriceStr = request.getParameter("orderDetails[" + i + "].roomPrice");
			detail.setRoomPrice(
					(roomPriceStr != null && !roomPriceStr.isEmpty()) ? Integer.valueOf(roomPriceStr) : 0);

			// 取得入住人數
			String numberOfPeopleStr = request.getParameter("orderDetails[" + i + "].numberOfPeople");
			detail.setNumberOfPeople(
					(numberOfPeopleStr != null && !numberOfPeopleStr.isEmpty()) ? Integer.valueOf(numberOfPeopleStr)
							: 0);

			// 取得房客姓名
			String roomGuestName = request.getParameter("orderDetails[" + i + "].roomGuestName");
			detail.setRoomGuestName(roomGuestName);

			// 取得特殊需求
			String specialReq = request.getParameter("orderDetails[" + i + "].specialReq");
			detail.setSpecialReq(specialReq);

			// 設定關聯的訂單
			detail.setRoomOrder(savedOrder);

			details.add(detail);
			i++;
		}

		return details;
	}

	// ===== 準備 bookPage 所需資料 =====
	private String prepareBookPageData(Model model, RoomOrder roomOrder) {
		// 取得房型、價格、專案名稱等資料
		Map<String, String> roomTypeNames = roomTypeSvc.getAllAvailableRoomTypes().stream()
				.collect(Collectors.toMap(
						roomType -> String.valueOf(roomType.getRoomTypeId()),
						RoomTypeVO::getRoomTypeName));
		Map<String, Integer> roomTypePrices = roomTypeSvc.getAllAvailableRoomTypes().stream()
				.collect(Collectors.toMap(
						roomType -> String.valueOf(roomType.getRoomTypeId()),
						RoomTypeVO::getRoomTypePrice));
		model.addAttribute("roomTypeNames", roomTypeNames);
		model.addAttribute("roomTypePrices", roomTypePrices);

		Map<String, String> packageNames = new HashMap<>();
		packageNames.put("800", "南島晨光專案");
		packageNames.put("1800", "蔚藍晨夕專案");
		packageNames.put("2800", "悠日饗茶專案");
		model.addAttribute("packageNames", packageNames);

		return "front-end/room/bookPage";
	}

	// 驗證碼驗證方法
	private boolean validateCaptcha(String userInput, String expectedCaptcha) {
		return userInput != null && userInput.equals(expectedCaptcha);
	}
}