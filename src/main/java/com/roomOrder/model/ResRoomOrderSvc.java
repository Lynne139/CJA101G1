package com.roomOrder.model;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

import java.util.Optional;

import java.time.LocalDate;
import com.resto.dto.RestoOrderFromRoomDTO;
import com.resto.entity.TimeslotVO;
import com.resto.integration.room.RestoPeriodCode;
import com.roomOList.model.RoomOList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

@Service
public class ResRoomOrderSvc {
    @Autowired
    private RoomOrderService orderService;

    @Autowired
    private com.resto.model.TimeslotService timeslotService;

    @Autowired
    private com.resto.model.ReservationService reservationService;

    public Map<String, Object> checkAndAutoAssignRestoTimeslots(
            Integer restoId, int numOfPeople, String checkInDate, String checkOutDate, int projectPlan) {

        // 1. 取得所有住宿日期
        LocalDate start = LocalDate.parse(checkInDate);
        LocalDate end = LocalDate.parse(checkOutDate);
        List<LocalDate> stayDates = new ArrayList<>();
        for (LocalDate d = start; d.isBefore(end); d = d.plusDays(1)) {
            stayDates.add(d);
        }

        // 2. 依方案決定要查哪些 period
        List<Integer> periodIds = getPeriodIdsByProjectPlan(projectPlan);

        // 3. 查詢每一天每個 period
        List<RestoOrderFromRoomDTO> result = new ArrayList<>();

        // 預先查出所有啟用時段，並依 periodId 分組
        List<TimeslotVO> allTimeslots = timeslotService.getAllEnabled();
        Map<Integer, List<TimeslotVO>> timeslotMapByPeriod = allTimeslots.stream()
                .filter(t -> t.getPeriodVO() != null)
                .collect(Collectors.groupingBy(t -> t.getPeriodVO().getPeriodId()));

        for (LocalDate date : stayDates) {
            for (Integer periodId : periodIds) {
                List<TimeslotVO> timeslots = timeslotMapByPeriod.getOrDefault(periodId, Collections.emptyList());

                Optional<TimeslotVO> availableSlot = timeslots.stream()
                        .filter(slot -> reservationService.getRemaining(restoId, slot.getTimeslotId(),
                                date) >= numOfPeople)
                        .findFirst();

                if (availableSlot.isPresent()) {
                    TimeslotVO slot = availableSlot.get();
                    RestoOrderFromRoomDTO dto = new RestoOrderFromRoomDTO();
                    dto.setRegiDate(date);
                    dto.setRestoId(restoId);
                    dto.setTimeslotId(slot.getTimeslotId());
                    result.add(dto);
                } else {
                    // 若任一天、某 period 沒有時段可用，可在這裡 early return
                    return Map.of("success", false, "message", "有日期/時段無法預約");
                }
            }
        }
        // 全部都找到可預約時段
        return Map.of("success", true, "data", result);
    }

    // 依方案取得 periodId 清單
    private List<Integer> getPeriodIdsByProjectPlan(int projectPlan) {
        if (projectPlan == 1)
            return List.of(RestoPeriodCode.BREAKFAST.getCode());
        if (projectPlan == 2)
            return List.of(RestoPeriodCode.BREAKFAST.getCode(), RestoPeriodCode.DINNER.getCode());
        if (projectPlan == 3)
            return List.of(RestoPeriodCode.BREAKFAST.getCode(), RestoPeriodCode.DINNER.getCode(),
                    RestoPeriodCode.TEA.getCode());
        return List.of();
    }

    /**
     * 取消加購專案，並更新訂單金額與 projectAddOn 狀態
     * @param order RoomOrder
     * @param details List<RoomOList>
     * @return 更新後的 RoomOrder
     */
    public RoomOrder cancelProjectAddOn(RoomOrder order, List<RoomOList> details) {
        int totalRoomPrice = 0;
        for (RoomOList detail : details) {
            totalRoomPrice += detail.getRoomPrice() != null ? detail.getRoomPrice() : 0;
        }
        // 計算加購專案價格
        Integer addonPrice = order.getTotalAmount() - totalRoomPrice;
        // 計算新總價
        Integer newTotal = totalRoomPrice - addonPrice;
        // 計算新實際支付金額
        Integer newActualAmount = newTotal - (order.getDiscountAmount() != null ? order.getDiscountAmount() : 0);
        // 更新訂單
        order.setTotalAmount(newTotal);
        order.setActualAmount(newActualAmount);
        order.setProjectAddOn(0);
        return orderService.save(order);
    }
}
