package com.roomOrder.controller;


import com.resto.model.RestoVO;
import com.roomOrder.model.RoomOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.roomOrder.model.RoomOrderService;

@Controller
@RequestMapping("/admin")
public class RoomOrderController {
    @Autowired
    RoomOrderService orderService;

    // ===== 新增 =====

    @GetMapping("/roomo_info/add")
public String showAddModal(Model model) {
    model.addAttribute("roomOrder", new RoomOrder());
    return "admin/fragments/roomo/modals/roomo_add :: addModalContent";
}

// @GetMapping("/roomo_info/edit")
// public String showEditModal(@RequestParam Integer roomOrderId, Model model) {
//     RoomOrder order = roomOrderService.findById(roomOrderId).orElseThrow();
//     model.addAttribute("roomOrder", order);
//     model.addAttribute("memberList", memberService.findAll());
//     model.addAttribute("couponList", couponService.findAll());
//     model.addAttribute("employeeList", employeeService.findAll());
//     return "admin/fragments/roomo/modals/roomo_edit :: editModalContent";
// }

// ===== 修改 =====
	//取得edit modal
	@GetMapping("/roomo_info/edit")
	public String showEditModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
	    RoomOrder roomOrder = orderService.getById(roomOrderId);

	    model.addAttribute("roomOrder", roomOrder);
	    return "admin/fragments/resto/modals/resto_edit :: editModalContent";
	}



    

}
