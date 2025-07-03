package com.roomOrder.controller;

import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;
import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.text.SimpleDateFormat;

@Controller
@RequestMapping("/member/room")
public class RoomOrderFrontController {
    @Autowired
    private RoomOrderService roomOrderService;
    @Autowired
    private RoomOListService roomOListService;
    @Autowired
    private MemberService memberService;

    // 會員訂單列表
    @GetMapping("/roomOrderView")
    public String memberOrders(HttpSession session, Model model) {
        // MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        // if (member == null) {
        //     return "redirect:/front-end/member/login";
        // }
        MemberVO member = memberService.getOneMember(1);
        List<RoomOrder> roomOrders = roomOrderService.getByMemberId(member.getMemberId());
        model.addAttribute("roomOrders", roomOrders);
        return "front-end/room/roomOrderView";
    }

    // 取得單筆訂單資料（for 編輯 modal）
    @GetMapping("/edit")
    public String showEditModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model, HttpSession session) {
        // MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        MemberVO member = memberService.getOneMember(1);
        RoomOrder order = roomOrderService.getById(roomOrderId);
        if (order == null || !order.getMember().getMemberId().equals(member.getMemberId())) {
            model.addAttribute("errorMessage", "查無此訂單");
            return "front-end/room/modal/order_edit :: editModalContent";
        }
        List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
        order.setOrderDetails(details);
        model.addAttribute("order", order);
        model.addAttribute("isAdmin", false);
        return "front-end/room/modal/order_edit :: editModalContent";
    }

    // 修改訂單（允許多明細欄位更新）
    @PostMapping("/update")
    public String updateOrder(@ModelAttribute RoomOrder roomOrder, HttpSession session, Model model) {
        // MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        MemberVO member = memberService.getOneMember(1);
        RoomOrder oldOrder = roomOrderService.getById(roomOrder.getRoomOrderId());
        if (oldOrder == null || !oldOrder.getMember().getMemberId().equals(member.getMemberId())) {
            model.addAttribute("errorMessage", "查無此訂單");
            return "front-end/room/roomOrderEditModal :: editModalContent";
        }
        // 只允許更新入住日、退房日、特殊需求、房客代表
        oldOrder.setCheckInDate(roomOrder.getCheckInDate());
        oldOrder.setCheckOutDate(roomOrder.getCheckOutDate());
        // 更新明細的特殊需求（假設只允許第一筆）
        if (roomOrder.getOrderDetails() != null && !roomOrder.getOrderDetails().isEmpty()) {
            RoomOList newDetail = roomOrder.getOrderDetails().get(0);
            List<RoomOList> oldDetails = oldOrder.getOrderDetails();
            if (oldDetails != null && !oldDetails.isEmpty()) {
                oldDetails.get(0).setSpecialReq(newDetail.getSpecialReq());
                oldDetails.get(0).setRoomGuestName(newDetail.getRoomGuestName());
            }
        }
        // 更新 updateDate 為現在（字串格式）
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        oldOrder.setUpdateDate(now);
        roomOrderService.save(oldOrder);
        return "redirect:/member/room/roomOrderView";
    }

    // 取消訂單
    @PostMapping("/cancel")
    @ResponseBody
    public String cancelOrder(@RequestParam Integer roomOrderId, HttpSession session) {
        // MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        MemberVO member = memberService.getOneMember(1);
        RoomOrder order = roomOrderService.getById(roomOrderId);
        if (order == null || !order.getMember().getMemberId().equals(member.getMemberId())) {
            return "fail";
        }
        order.setOrderStatus(0); // 0:取消
        roomOrderService.save(order);
        return "success";
    }

    // 會員訂單檢視（for 檢視 modal）
    @GetMapping("/view")
    public String showViewModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model, HttpSession session) {
        MemberVO member = memberService.getOneMember(1);
        RoomOrder order = roomOrderService.getById(roomOrderId);
        if (order == null || !order.getMember().getMemberId().equals(member.getMemberId())) {
            model.addAttribute("errorMessage", "查無此訂單");
            return "front-end/room/modal/order_view :: viewModalContent";
        }
        List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);
        model.addAttribute("roomOrder", order);
        model.addAttribute("roomOLists", details);
        return "front-end/room/modal/order_view :: viewModalContent";
    }
} 