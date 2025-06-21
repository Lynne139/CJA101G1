package com.roomOrder.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.roomOrder.model.RoomOrderService;

@Controller
@RequestMapping("/RoomOrder")
public class RoomOrderController {
    private final RoomOrderService service;

    public RoomOrderController(RoomOrderService service) {
        this.service = service;
    }

    

}
