package com.roomOrder.controller;

import com.member.model.MemberVO;
import com.roomOrder.model.RoomOrder;
import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import com.roomtypeschedule.model.RoomTypeScheduleService;
import com.roomtypeschedule.model.RoomTypeScheduleVO;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.roomOrder.model.RoomOrderService;
import com.roomtype.model.RoomTypeVO;
import com.coupon.entity.Coupon;
import com.coupon.service.CouponService;
import com.coupon.service.MemberCouponService;
import com.member.model.MemberService;

@Controller
@RequestMapping("/admin")
public class RoomOrderController {
	@Autowired
	RoomOrderService orderService;

	@Autowired
	private com.roomtype.model.RoomTypeService roomTypeService;

	@Autowired
	private RoomOListService roomOListService;

	@Autowired
	private MemberCouponService memberCouponService;

	@Autowired
	private RoomTypeScheduleService roomScheduleService;

	// ===== 新增 =====

	@GetMapping("/roomo_info/add")
	public String showAddModal(Model model) {
		model.addAttribute("roomOrder", new RoomOrder());
		model.addAttribute("roomTypeList", roomTypeService.getAll()); // 加這行

		return "admin/fragments/roomo/modals/roomo_add :: addModalContent";
	}

	@GetMapping("/roomo_info/check_schedule")
	@ResponseBody
	public Map<String, Object> checkRoomAvailability(@RequestParam("roomTypeId") Integer roomTypeId,
			@RequestParam("checkInDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkInDate,
			@RequestParam("checkOutDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date checkOutDate) {

		Map<String, Object> result = new HashMap<>();

		RoomTypeVO roomTypeVO = roomTypeService.getOneRoomType(roomTypeId);
		int totalRooms = roomTypeVO.getRoomTypeAmount();

		List<String> fullyBookedDates = new ArrayList<>();

		LocalDate start = checkInDate.toLocalDate();
		LocalDate end = checkOutDate.toLocalDate();

		while (!start.isAfter(end.minusDays(1))) {
			Date currentDate = Date.valueOf(start);
			Optional<RoomTypeScheduleVO> optional = roomScheduleService
					.getByRoomTypeVO_RoomTypeIdAndRoomOrderDate(roomTypeId, currentDate);

			int booked = optional.map(RoomTypeScheduleVO::getRoomRSVBooked).orElse(0);
			int remaining = totalRooms - booked;

			if (remaining <= 0) {
				fullyBookedDates.add(currentDate.toString());
			}

			start = start.plusDays(1);
		}

		if (!fullyBookedDates.isEmpty()) {
			result.put("available", false);
			result.put("message", "以下日期已滿房，請重新選擇其他日期");
			result.put("unavailableDates", fullyBookedDates);
		} else {
			result.put("available", true);
			result.put("message", "選定區間內皆有空房");
		}

		return result;
	}

	// ===== 修改 =====
	// 取得edit modal
	@GetMapping("/roomo_info/edit")
	public String showEditModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
		RoomOrder roomOrder = orderService.getById(roomOrderId);
		List<RoomOList> orderDetails = roomOListService.findByRoomOrderId(roomOrderId);
		roomOrder.setOrderDetails(orderDetails != null ? orderDetails : new ArrayList<>());
		model.addAttribute("roomTypeList", roomTypeService.getAll());
		model.addAttribute("roomOrder", roomOrder);

		// 查詢該會員可用的優惠券
		Integer memberId = roomOrder.getMember() != null ? roomOrder.getMember().getMemberId() : null;
		Integer priceBeforeUsingCoupon = roomOrder.getRoomAmount(); // 或你要的金額欄位
		List<Coupon> couponList = memberCouponService.getRoomOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
		model.addAttribute("couponList", couponList);

		return "admin/fragments/roomo/modals/roomo_edit :: editModalContent";
	}

	// 更新訂單
	@PostMapping("/roomo_info/update")
	public String updateOrder(
			@ModelAttribute RoomOrder roomOrder,
			HttpServletRequest request,
			RedirectAttributes redirectAttributes) {

		roomOrder.setUpdateDate(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));

		// 1. 更新主表
		RoomOrder updatedOrder = orderService.updateOrder(roomOrder);

		// 2. 取得明細資料
		List<RoomOList> details = new ArrayList<>();
		int i = 0;
		while (request.getParameter("orderDetails[" + i + "].roomTypeId") != null) {
			RoomOList detail = new RoomOList();

			// 取得明細主鍵
			String idStr = request.getParameter("orderDetails[" + i + "].roomOrderListId");
			if (idStr != null && !idStr.isEmpty()) {
				detail.setRoomOrderListId(Integer.valueOf(idStr));
				// 既有明細，查出原本的 createDate
				RoomOList old = roomOListService.findByRoomOrderListId(detail.getRoomOrderListId());
				if (old != null) {
					detail.setCreateDate(old.getCreateDate());
				} else {
					detail.setCreateDate(
							new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
				}
			} else {
				// 新增明細，設現在時間
				detail.setCreateDate(
						new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
			}

			// 其他欄位
			detail.setListStatus(request.getParameter("orderDetails[" + i + "].listStatus"));
			Integer roomTypeId = Integer.valueOf(request.getParameter("orderDetails[" + i + "].roomTypeId"));
			detail.setRoomTypeId(roomTypeId);

			RoomTypeVO roomType = roomTypeService.getOneRoomType(roomTypeId);
			detail.setRoomType(roomType);

			String roomAmountStr = request.getParameter("orderDetails[" + i + "].roomAmount");
			detail.setRoomAmount(
					(roomAmountStr != null && !roomAmountStr.isEmpty()) ? Integer.valueOf(roomAmountStr) : 0);

			String roomPriceStr = request.getParameter("orderDetails[" + i + "].roomPrice");
			detail.setRoomPrice((roomPriceStr != null && !roomPriceStr.isEmpty()) ? Integer.valueOf(roomPriceStr) : 0);

			String numberOfPeopleStr = request.getParameter("orderDetails[" + i + "].numberOfPeople");
			detail.setNumberOfPeople(
					(numberOfPeopleStr != null && !numberOfPeopleStr.isEmpty()) ? Integer.valueOf(numberOfPeopleStr)
							: 0);

			detail.setRoomGuestName(request.getParameter("orderDetails[" + i + "].roomGuestName"));
			detail.setSpecialReq(request.getParameter("orderDetails[" + i + "].specialReq"));
			detail.setRoomOrder(updatedOrder);

			// 3. 判斷是新增還是更新
			if (detail.getRoomOrderListId() != null) {
				// 已有主鍵，更新
				roomOListService.save(detail);
			} else {
				// 沒有主鍵，新增
				roomOListService.save(detail);
			}

			i++;
		}

		redirectAttributes.addFlashAttribute("success", "訂單更新成功！");
		return "redirect:/admin/roomo_info";
	}

	// ===== 填入ID查詢會員名稱 =====

	@Autowired
	MemberService memberService;

	@Autowired
	private CouponService couponService;

	@GetMapping("/member/name")
	@ResponseBody
	public Map<String, String> getMemberName(@RequestParam("memberId") Integer memberId) {
		MemberVO member = memberService.getOneMember(memberId);
		Map<String, String> response = new HashMap<>();
		if (member != null) {
			response.put("memberId", member.getMemberId().toString());
			response.put("memberName", member.getMemberName());
		} else {
			response.put("memberId", "");
			response.put("memberName", "查無此會員");
		}
		return response;
	}

	// 新增訂單（含多筆明細）
	@PostMapping("/roomo_info/insert")
	public String insertOrderAndDetails(
			@ModelAttribute RoomOrder roomOrder,
			HttpServletRequest request,
			Model model) {

		// 取得 memberId
		String memberIdStr = request.getParameter("memberId");
		if (memberIdStr != null && !memberIdStr.isEmpty()) {
			Integer memberId = Integer.valueOf(memberIdStr);
			MemberVO member = memberService.getOneMember(memberId);
			roomOrder.setMember(member);
		}

		// 取得 couponCode
		String couponCode = request.getParameter("couponCode");
		if (couponCode != null && !couponCode.isEmpty()) {
			String couponCodeTrimmed = couponCode.trim();
			Coupon coupon = couponService.getCouponByCode(couponCodeTrimmed)
					.orElse(null); // 如果找不到就設為 null
			roomOrder.setCoupon(coupon);
		} else {
			roomOrder.setCoupon(null); // 沒有券就設為 null
		}

		// 1. 先存主表
		RoomOrder savedOrder = orderService.save(roomOrder);

		// 2. 逐筆取得明細欄位
		List<RoomOList> details = parseOrderDetailsFromRequest(request, savedOrder);
		for (RoomOList detail : details) {
			roomOListService.save(detail);
		}

		return "redirect:/admin/roomo_info";
	}

	// ===== 刪除 =====
	@PostMapping("/roomo_info/delete")
	@ResponseBody
	public Map<String, Object> deleteroomo(@RequestParam("roomOrderId") Integer id) {
		Map<String, Object> result = new HashMap<>();
		try {
			orderService.deleteById(id);
			result.put("success", true);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", e.getMessage());
		}
		return result;
	}
	
	// ===== 取消訂單 =====
	@PostMapping("/roomo_info/cancel")
    @ResponseBody
    public Map<String, Object> cancelRoomOrder(@RequestParam Integer roomOrderId) {
        RoomOrder order = orderService.getById(roomOrderId);
        if (order != null) {
            order.setOrderStatus(0); // 0:取消
            List<Integer> olistIds = new ArrayList<>();
            if (order.getOrderDetails() != null) {
                for (RoomOList detail : order.getOrderDetails()) {
                    detail.setListStatus("0"); // 0:取消
                    roomOListService.save(detail);
                    olistIds.add(detail.getRoomOrderListId());
                }
            }
            orderService.save(order);
            return Map.of("success", true, "olistIds", olistIds);
        }
        return Map.of("success", false, "message", "找不到訂單");
    }

	// ===== 查詢會員擁有的所有適用優惠券列表 =====

	@GetMapping("/roomo_info/member_coupons")
	@ResponseBody
	public List<Coupon> getMemberCoupons(@RequestParam("memberId") Integer memberId,
			@RequestParam("roomAmount") Integer priceBeforeUsingCoupon) {
		return memberCouponService.getRoomOrderApplicableCoupons(memberId, priceBeforeUsingCoupon);
	}

	private List<RoomOList> parseOrderDetailsFromRequest(HttpServletRequest request, RoomOrder savedOrder) {
		List<RoomOList> details = new ArrayList<>();
		int i = 0;
		while (request.getParameter("orderDetails[" + i + "].roomTypeId") != null) {
			RoomOList detail = new RoomOList();

			// === 補這段 ===
			String idStr = request.getParameter("orderDetails[" + i + "].roomOrderListId");
			if (idStr != null && !idStr.isEmpty()) {
				detail.setRoomOrderListId(Integer.valueOf(idStr));
			}
			// ============

			detail.setListStatus("1"); // 預設狀態
			Integer roomTypeId = Integer.valueOf(request.getParameter("orderDetails[" + i + "].roomTypeId"));
			detail.setRoomTypeId(roomTypeId);

			RoomTypeVO roomType = roomTypeService.getOneRoomType(roomTypeId);
			detail.setRoomType(roomType);

			String roomAmountStr = request.getParameter("orderDetails[" + i + "].roomAmount");
			detail.setRoomAmount(
					(roomAmountStr != null && !roomAmountStr.isEmpty()) ? Integer.valueOf(roomAmountStr) : 0);

			String roomPriceStr = request.getParameter("orderDetails[" + i + "].roomPrice");
			detail.setRoomPrice((roomPriceStr != null && !roomPriceStr.isEmpty()) ? Integer.valueOf(roomPriceStr) : 0);

			String numberOfPeopleStr = request.getParameter("orderDetails[" + i + "].numberOfPeople");
			detail.setNumberOfPeople(
					(numberOfPeopleStr != null && !numberOfPeopleStr.isEmpty()) ? Integer.valueOf(numberOfPeopleStr)
							: 0);

			detail.setRoomGuestName(request.getParameter("orderDetails[" + i + "].roomGuestName"));
			detail.setSpecialReq(request.getParameter("orderDetails[" + i + "].specialReq"));
			detail.setRoomOrder(savedOrder);
			details.add(detail);
			i++;
		}
		return details;
	}

	// ===== 檢視訂單 =====
	@GetMapping("/roomo_info/view")
	public String viewRoomOrder(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
		RoomOrder order = orderService.getById(roomOrderId);
		List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
		model.addAttribute("roomOrder", order);
		model.addAttribute("roomOLists", details);
		return "admin/fragments/roomo/modals/roomo_view :: viewModalContent";
	}

	
}
