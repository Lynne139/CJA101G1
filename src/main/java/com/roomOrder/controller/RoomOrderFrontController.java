package com.roomOrder.controller;

import com.coupon.entity.Coupon;
import com.coupon.service.MemberCouponService;
import com.member.model.MemberService;
import com.member.model.MemberVO;
import com.notification.service.NotificationService;
import com.resto.model.RestoOrderService;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;
import com.roomtype.model.RoomTypeService;
import com.roomtype.model.RoomTypeVO;
import com.roomtypeschedule.model.RoomTypeScheduleService;
import com.roomtypeschedule.model.RoomTypeScheduleVO;
import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.stream.Collectors;
import com.resto.model.ReservationService;
import com.resto.model.TimeslotService;
import com.resto.dto.RestoOrderFromRoomDTO;
import com.resto.entity.TimeslotVO;
import com.resto.integration.room.RoomOrderTxService;

@Controller
@RequestMapping("/member/order")
public class RoomOrderFrontController {
    @Autowired
    private RoomOrderService roomOrderService;
    @Autowired
    private RoomOListService roomOListService;
    @Autowired
    private RestoOrderService restoOrderService;
    @Autowired
    private RoomTypeService roomTypeService;
    @Autowired
    private RoomTypeScheduleService roomScheduleService;
    @Autowired
    private ReservationService reservationService;
    @Autowired
    private MemberCouponService memberCouponService;
    @Autowired
    private MemberService memberService;
    @Autowired
    private NotificationService notificationService;


    // 會員訂單列表
    @GetMapping("/roomOrder")
    public String memberOrders(HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front-end/member/login";
        }
        // MemberVO member = memberService.getOneMember(1);
        List<RoomOrder> roomOrders = roomOrderService.getByMemberId(member.getMemberId());
        model.addAttribute("roomOrders", roomOrders);
        return "front-end/room/roomOrderView";
    }

    // 取得單筆訂單資料（for 編輯 modal）
    @GetMapping("/roomOrder/edit")
    public String showEditModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        if (member == null) {
            return "redirect:/front-end/member/login";
        }
        // MemberVO member = memberService.getOneMember(1);
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
    @PostMapping("/roomOrder/update")
    public String updateOrder(@ModelAttribute RoomOrder roomOrder, HttpSession session, Model model) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        // MemberVO member = memberService.getOneMember(1);
        RoomOrder oldOrder = roomOrderService.getById(roomOrder.getRoomOrderId());
        if (oldOrder == null || !oldOrder.getMember().getMemberId().equals(member.getMemberId())) {
            model.addAttribute("errorMessage", "查無此訂單");
            return "front-end/room/roomOrderEditModal :: editModalContent";
        }
        // 取得舊明細
        List<RoomOList> oldDetails = roomOListService.findByRoomOrderId(oldOrder.getRoomOrderId());
        oldOrder.setOrderDetails(oldDetails);
        System.out.println("✅ 舊明細：" + oldDetails);

        // 更新明細的特殊需求（多筆）
        if (roomOrder.getOrderDetails() != null && !roomOrder.getOrderDetails().isEmpty()) {
            List<RoomOList> newDetails = roomOrder.getOrderDetails();
            System.out.println("✅ 舊明細：" + oldDetails);
            if (oldDetails != null && !oldDetails.isEmpty()) {
                Map<Integer, RoomOList> oldDetailMap = oldDetails.stream()
                        .collect(Collectors.toMap(RoomOList::getRoomOrderListId, d -> d));
                for (RoomOList newDetail : newDetails) {
                    RoomOList oldDetail = oldDetailMap.get(newDetail.getRoomOrderListId());
                    if (oldDetail != null) {
                        oldDetail.setSpecialReq(newDetail.getSpecialReq());
                        oldDetail.setRoomGuestName(newDetail.getRoomGuestName());
                        roomOListService.save(oldDetail);
                        System.out.println("✅ 更新明細：" + oldDetail.getRoomOrderListId());
                    }
                }
            }
        }

        // 更新 updateDate 為現在（字串格式）
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        oldOrder.setUpdateDate(now);
        roomOrderService.save(oldOrder);

        System.out.println("✅ 收到更新請求：" + roomOrder.getRoomOrderId());
        System.out.println("入住：" + roomOrder.getCheckInDate());
        return "redirect:/member/order/roomOrder";
    }

    // 取消訂單
    @GetMapping("/roomOrder/cancel")
    @ResponseBody
    public String cancelOrder(@RequestParam Integer roomOrderId, HttpSession session) {
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
        RoomOrder order = roomOrderService.getById(roomOrderId);
        List<RoomOList> details = roomOListService.findByRoomOrderId(roomOrderId);

        // if (order.getProjectAddOn() == 1) {
        //     // 取消餐廳訂單
        //     restoOrderService.cancelByRoomOrderId(roomOrderId);
        // }

        // 取消住宿訂單
        if (details.size() > 0) {
            for (RoomOList detail : details) {
                detail.setListStatus(0);
            }
        }
        if (order == null || !order.getMember().getMemberId().equals(member.getMemberId())) {
            return "fail";
        }
        order.setOrderStatus(0); // 0:取消
        order.setPayStatus("2"); // 2:已退款
        roomOrderService.save(order);
        // 更新會員累計金額
        Integer memberId = order.getMember().getMemberId();
        Integer rollbackAmount = -order.getActualAmount();
        memberService.updateConsumptionAndLevelAndPoints(memberId, rollbackAmount);
        // 發送通知
        notificationService.createNotification(memberId, "訂單取消", "已成功取消訂單，請至訂單查詢頁面查看。");

        return "redirect:/member/center";
    }

    // 會員訂單檢視（for 檢視 modal）
    @GetMapping("/roomOrder/view")
    public String showViewModal(@RequestParam("roomOrderId") Integer roomOrderId, Model model, HttpSession session) {
        // MemberVO member = memberService.getOneMember(1);
        MemberVO member = (MemberVO) session.getAttribute("loggedInMember");
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

    // 搜尋可預訂房間數量
    @GetMapping("/roomOrder/{roomTypeId}/check_schedule")
    @ResponseBody
    public Integer getRoomInventoryRange(@PathVariable Integer roomTypeId,
            @RequestParam String start,
            @RequestParam String end) {
        java.sql.Date startDate = java.sql.Date.valueOf(start);
        java.sql.Date endDate = java.sql.Date.valueOf(end);

        RoomTypeVO roomType = roomTypeService.getOneRoomType(roomTypeId);
        List<RoomTypeScheduleVO> schedules = roomScheduleService
                .findSchedules(roomType, startDate, endDate);

        // 找區間內的最少可訂
        int minRemaining = schedules.stream()
                .mapToInt(s -> s.getRoomAmount() - s.getRoomRSVBooked())
                .min()
                .orElse(0);

        return minRemaining;
    }

    // 查詢餐廳時段剩餘名額（轉發 ReservationController 的 API）
    @GetMapping("/roomOrder/meal/remaining")
    @ResponseBody
    public Integer getMealRemaining(@RequestParam Integer restoId,
            @RequestParam Integer timeslotId,
            @RequestParam String date) {
        // 這裡直接呼叫 reservationService.getRemaining
        // 需將 date 轉為 LocalDate
        java.time.LocalDate localDate = java.time.LocalDate.parse(date);
        int remaining = reservationService.getRemaining(restoId, timeslotId, localDate);
        return remaining;
    }
    
}