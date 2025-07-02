package com.resto.integration.room;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.entity.RestoOrderVO;
import com.resto.entity.RestoVO;
import com.resto.entity.TimeslotVO;
import com.resto.model.ReservationService;
import com.resto.model.RestoOrderRepository;
import com.resto.model.RestoOrderService;
import com.resto.model.RestoService;
import com.resto.model.TimeslotService;
import com.resto.utils.RestoOrderSource;
import com.resto.utils.RestoOrderStatus;
import com.roomOList.model.RoomOList;
import com.roomOrder.model.RoomOrder;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RestoOrderFacade {

	@Autowired
    RestoOrderService restoSvc;
	@Autowired
    ReservationService reservationSvc;
	@Autowired
    RestoService restoService;
	@Autowired
    TimeslotService timeslotService;
	@Autowired
	RestoOrderRepository restoOrderRepository;

	
	// 新增餐廳訂單入資料庫
    @Transactional
    public List<Integer> createBatchFromRoom(RoomOrder roomOrder,
    										 RoomOList roomOList,
                                             int highChairs,
                                             String restoReq,
                                             List<RestoOrderFromRoomDTO> meals
        ){

        if (meals == null || meals.isEmpty())
            throw new BadParameterException("請提供餐廳用餐資訊");

        List<Integer> ids = new ArrayList<>();

        
        // 批次處理每一餐(ex.一泊二食產生兩張餐廳訂位訂單)
        for (RestoOrderFromRoomDTO m : meals){

            // ========= 驗證欄位 =========
            if (m.getRestoId()==null || m.getTimeslotId()==null || m.getRegiDate()==null){
                throw new BadParameterException("餐廳 / 時段 / 日期 皆為必填");
            }

            // ========= 餘位驗證 =========
            int remaining = reservationSvc.getRemaining(
                    m.getRestoId(), m.getTimeslotId(), m.getRegiDate());
            if (roomOList.getNumberOfPeople() > remaining){
                throw new SeatExceedException("該時段只剩"+remaining+"位");
            }

            // ========= 組餐廳訂單 =========
            RestoOrderVO o = new RestoOrderVO();
            
            // DTO欄位
            o.setRestoVO(restoService.getById(m.getRestoId()));
            o.setRegiDate(m.getRegiDate());
            o.setTimeslotVO(timeslotService.getById(m.getTimeslotId()));
            
            // 其他參數抓取欄位
            o.setRoomOrder(roomOrder);
            o.setMemberVO(roomOrder.getMember());
            o.setRegiSeats(roomOList.getNumberOfPeople());
            o.setHighChairs(highChairs);
            o.setRegiReq(restoReq);
            
            o.setOrderGuestName(roomOrder.getMember().getMemberName());
            o.setOrderGuestPhone(roomOrder.getMember().getMemberPhone());
            o.setOrderGuestEmail(roomOrder.getMember().getMemberEmail());

            o.setOrderSource(RestoOrderSource.ROOM);
            o.setOrderStatus(RestoOrderStatus.CREATED);
            

            restoSvc.insert(o);
            ids.add(o.getRestoOrderId());
        }
        
        return ids;
    }
    
    // 更新餐廳訂單入資料庫
    @Transactional
    public List<Integer> updateBatchFromRoom(RoomOrder roomOrder,
                                             RoomOList roomOList,
                                             int highChairs,
                                             String restoReq,
                                             List<RestoOrderFromRoomDTO> meals
    ) {
        if (meals == null || meals.isEmpty())
            throw new BadParameterException("請提供餐廳用餐資訊");

        List<Integer> ids = new ArrayList<>();

        for (RestoOrderFromRoomDTO m : meals) {

            // ========= 驗證欄位 =========
            if (m.getRestoId() == null || m.getTimeslotId() == null || m.getRegiDate() == null) {
                throw new BadParameterException("餐廳 / 時段 / 日期 皆為必填");
            }

            // ========= 餘位驗證 =========
            int remaining = reservationSvc.getRemaining(m.getRestoId(), m.getTimeslotId(), m.getRegiDate());
            if (roomOList.getNumberOfPeople() > remaining) {
                throw new SeatExceedException("該時段只剩" + remaining + "位");
            }

            // ========= 抓原始訂單 =========
            RestoOrderVO o = restoSvc.getById(m.getRestoOrderId());
            if (o == null)
                throw new BadParameterException("找不到原始訂單，ID: " + m.getRestoOrderId());

            // ========= 覆寫資料 =========
            o.setRestoVO(restoService.getById(m.getRestoId()));
            o.setRegiDate(m.getRegiDate());
            o.setTimeslotVO(timeslotService.getById(m.getTimeslotId()));

            o.setRoomOrder(roomOrder);
            o.setMemberVO(roomOrder.getMember());
            o.setRegiSeats(roomOList.getNumberOfPeople());
            o.setHighChairs(highChairs);
            o.setRegiReq(restoReq);

            o.setOrderGuestName(roomOrder.getMember().getMemberName());
            o.setOrderGuestPhone(roomOrder.getMember().getMemberPhone());
            o.setOrderGuestEmail(roomOrder.getMember().getMemberEmail());

            // 狀態不變
            // o.setOrderSource(...); 不需要更動
            // o.setOrderStatus(...); 不允許復原取消不需設定

            restoSvc.update(o);
            ids.add(o.getRestoOrderId());
        }

        return ids;
    }

    
  
    
    
    
    
    
    
}
