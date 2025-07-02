package com.roomOList.controller;

import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;
import com.roomOrder.model.RoomOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // @GetMapping("/roomo_info/view")
    // public String viewRoomOrder(@RequestParam("roomOrderId") Integer roomOrderId, Model model) {
    //     List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
    //     model.addAttribute("roomOLists", details);
    //     return "admin/fragments/roomo/modals/roomo_view :: viewModalContent";
    // }

    // 刪除單筆訂單明細
    @PostMapping("/roomo_info/roomOlist/delete")
    @ResponseBody
    public Map<String, Object> deleteRoomOList(
            @RequestParam(value = "roomOrderListId", required = false) Integer roomOrderListId) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (roomOrderListId == null) {
                // 若 id 為空，直接回傳成功，不做任何事
                result.put("success", true);
            } else {
                // 查詢該明細
                RoomOList detail = roomOListService.findByRoomOrderListId(roomOrderListId);
                if (detail == null) {
                    result.put("success", false);
                    result.put("message", "找不到該訂單明細");
                    return result;
                }
                RoomOrder roomOrder = detail.getRoomOrder();
                Integer roomOrderId = roomOrder.getRoomOrderId();
                // 查詢該訂單下所有明細數量
                List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
                if (details == null || details.size() <= 1) {
                    result.put("success", false);
                    result.put("message", "一張訂單至少要有一筆訂單明細，無法刪除最後一筆！");
                    return result;
                }
                roomOListService.deleteByRoomOrderListId(roomOrderListId);
                result.put("success", true);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
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
