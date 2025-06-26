package com.roomOrder.controller;

import com.member.model.MemberVO;
//import com.room.model.RoomVO;
import com.roomOrder.model.RoomOrder;
import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.roomOrder.model.RoomOrderService;
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

	// ===== 新增 =====

	@GetMapping("/roomo_info/add")
	public String showAddModal(Model model) {
		model.addAttribute("roomOrder", new RoomOrder());
		model.addAttribute("roomTypeList", roomTypeService.getAll()); // 加這行
		return "admin/fragments/roomo/modals/roomo_add :: addModalContent";
	}

	// ===== 修改 =====
	// 取得edit modal
	@GetMapping("/roomo_info/edit")
	public String showEditModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
		RoomOrder roomOrder = orderService.getById(roomOrderId);

		model.addAttribute("roomOrder", roomOrder);
		return "admin/fragments/roomo/modals/roomo_edit :: editModalContent";
	}

	@GetMapping("/roomo_info/view")
	public String viewRoomOrder(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
		RoomOrder roomOrder = orderService.getById(roomOrderId);
		model.addAttribute("roomOrder", roomOrder);
		return "admin/fragments/roomo/modals/roomo_view :: viewModalContent";
	}

	// ===== 填入ID查詢會員名稱 =====

	@Autowired
	MemberService memberService;

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
			@RequestParam("orderDetails") List<RoomOList> orderDetails,
			Model model) {

		// 1. 先存主表（訂單）
		RoomOrder savedOrder = orderService.save(roomOrder);

		// 2. 存明細表（訂單明細），每筆都設 roomOrder
		for (RoomOList detail : orderDetails) {
			detail.setRoomOrder(savedOrder);
			roomOListService.save(detail);
		}

		// 3. 回到列表或顯示成功
		return "redirect:/admin/roomo_info/list";
	}

}
