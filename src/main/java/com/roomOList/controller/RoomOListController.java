package com.roomOList.controller;

import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class RoomOListController {

    @Autowired
    private RoomOListService roomOListService;

    // 新增多筆訂單明細
    @PostMapping("/roomOList/insert")
    public String insertOrderDetails(@ModelAttribute("orderDetails") RoomOListForm form, Model model) {
        List<RoomOList> details = form.getOrderDetails();
        for (RoomOList detail : details) {
            roomOListService.save(detail);
        }
        // 新增成功後導回列表或顯示成功訊息
        return "redirect:/admin/roomOList/list";
    }

    @GetMapping("/roomo_info/view")
    public String viewRoomOrder(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
        List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
        model.addAttribute("roomOLists", details);
        return "admin/fragments/roomo/modals/roomo_view :: viewModalContent";
    }

    // // 顯示訂單明細列表
    // @GetMapping("/list")
    // public String listOrderDetails(Model model) {
    // List<RoomOList> orderDetails = roomOListService.findAll();
    // model.addAttribute("orderDetails", orderDetails);
    // return "admin/roomOList/list"; // 返回訂單明細列表的視
    // }

}

// 輔助表單物件
class RoomOListForm {
    private List<RoomOList> orderDetails;

    public List<RoomOList> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<RoomOList> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
