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

	@PostMapping("/orderInfo")
	public String showOrderInfo(@RequestParam Map<String, String> params,
			Model model,
			HttpSession session) {
		MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
		// if (member != null) {
		// member = memberService.getOneMember(member.getMemberId());
		// }

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
		roomOrder.setActualAmount(totalRoomAmount + packageSubtotal);
		roomOrder.setOrderDetails(orderDetails);

		// 模板顯示
		model.addAttribute("roomOrder", roomOrder);
		model.addAttribute("projectAddOnName", packageNames.get(params.get("package"))); // 不動 VO
		model.addAttribute("params", params); // 如果頁面還在用 params
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

	@GetMapping("/orderInfo/confirm")
	public String showConfirmPage(HttpSession session, Model model) {
		RoomOrder confirmedOrder = (RoomOrder) session.getAttribute("confirmedOrder");
		if (confirmedOrder == null) {
			model.addAttribute("errorMsg", "查無訂單資料，請重新下單。");
			return "front-end/room/bookPage";
		}
		model.addAttribute("roomOrder", confirmedOrder);
		session.removeAttribute("confirmedOrder");
		return "front-end/room/orderConfirm";
	}

	// ===== 儲存訂單 =====

	@Autowired
	private RoomOListService roomOListService;

	@Autowired
	private RoomTypeService roomTypeService;

	@Autowired
	private RoomTypeScheduleService roomScheduleService;

	@Autowired
	private NotificationService notificationService;

	@PostMapping("/orderInfo/confirm")
	public String createOrder(@ModelAttribute RoomOrder roomOrder,
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
			return "front-end/room/bookPage";
		}

		try {
			// 設定訂單基本資料
			roomOrder.setOrderStatus(1); // 設定訂單狀態為已確認
			roomOrder.setOrderDate(LocalDate.now().toString()); // 設定訂單日期

			// 處理折價券
			String couponCode = request.getParameter("couponCode");
			if (couponCode != null && !couponCode.trim().isEmpty()) {
				// 查詢折價券資料並設定
				Coupon coupon = couponService.getCouponByCode(couponCode).orElse(null);
				if (coupon != null) {
					roomOrder.setCoupon(coupon);
					// 計算折扣後的實際金額
					Integer originalAmount = roomOrder.getActualAmount();
					Integer discountAmount = Integer.valueOf(request.getParameter("discountAmount"));
					roomOrder.setActualAmount(originalAmount - discountAmount);
				}
			}

			// 付款方式檢查
			String payMethodStr = request.getParameter("payMethod");
			Integer payMethod = null;
			try {
				payMethod = Integer.valueOf(payMethodStr);
				if (payMethod != 0 && payMethod != 1) throw new NumberFormatException();
			} catch (Exception e) {
				model.addAttribute("errorMsg", "付款方式有誤，請重新選擇");
				// 補齊所有頁面資料再回原頁
				return "front-end/room/bookPage";
			}

			// 儲存主訂單
			RoomOrder savedOrder = roomOrderService.save(roomOrder);

			// 2. 解析並儲存訂單明細
			List<RoomOList> details = parseOrderDetailsFromRequest(request, savedOrder);
			for (RoomOList detail : details) {
				roomOListService.save(detail);
			}

			// 更新庫存
			for (RoomOList detail : details) {
				roomScheduleService.reserve(detail);
			}

			// 更新會員資料
			memberService.updateConsumptionAndLevelAndPoints(member.getMemberId(), savedOrder.getActualAmount());

			// 發通知
			notificationService.createNotification(
					member.getMemberId(), "訂單成立", "訂單已成立，請至訂單查詢頁面查看");

			// 處理折價券使用
			if (couponCode != null && !couponCode.trim().isEmpty()) {
				memberCouponService.useCoupon(member.getMemberId(), couponCode);
			}

			// 將訂單資料存入 session 供確認頁面使用
			session.setAttribute("confirmedOrder", savedOrder);

			return "front-end/member/memberCenter";

		} catch (Exception e) {
			e.printStackTrace();
			// 直接 forward 回訂房頁，帶錯誤訊息與原資料
			model.addAttribute("errorMsg", "訂單建立失敗：" + e.getMessage());
			model.addAttribute("roomOrder", roomOrder);
			// 如有 nights、params 也一併補齊
			// model.addAttribute("nights", nights);
			// model.addAttribute("params", params);

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

	// 可能需要的額外方法
	private boolean validateCaptcha(String userInput, String expectedCaptcha) {
		// 實作驗證碼驗證邏輯
		// 這裡假設驗證碼存在session中
		return userInput != null && userInput.equals(expectedCaptcha);
	}

}
