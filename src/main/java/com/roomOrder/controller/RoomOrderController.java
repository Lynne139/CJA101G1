package com.roomOrder.controller;


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

    //==========新增=============
    @GetMapping("room_order/add_Order")
    public String addOrder(@ModelAttribute("order") RoomOrder order, Model model) {
        return "redirect:/admin/roomo_info";
    }



    

}
