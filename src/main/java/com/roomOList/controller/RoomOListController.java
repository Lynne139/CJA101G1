package com.roomOList.controller;

import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/admin/roomOList")
public class RoomOListController {

    @Autowired
    private RoomOListService roomOListService;

    // 新增多筆訂單明細
    @PostMapping("/insert")
    public String insertOrderDetails(@ModelAttribute("orderDetails") RoomOListForm form, Model model) {
        List<RoomOList> details = form.getOrderDetails();
        for (RoomOList detail : details) {
            roomOListService.save(detail);
        }
        // 新增成功後導回列表或顯示成功訊息
        return "redirect:/admin/roomOList/list";
    }
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
