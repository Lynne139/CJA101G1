package com.resto.integration.room;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.roomOList.model.RoomOList;
import com.roomOrder.model.RoomOrder;
import com.roomOrder.model.RoomOrderService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoomOrderTxService {
	
	@Autowired
	RoomOrderService roomSvc;
	@Autowired
    RestoOrderFacade restoFacade;
	
    @Transactional // 確保住房訂單與餐廳訂單綁定為同批交易(一起成功或rollback)
    public RoomOrder createRoomAndMeals(RoomOrder roomOrder,
    									RoomOList roomOList,
                                        int highChairs,
                                        String req,
                                        List<RestoOrderFromRoomDTO> meals,
                                        boolean addMeal
        ){

        // 先存住房主表
        RoomOrder saved = roomSvc.save(roomOrder);

        // 需要加購才批次建立餐廳訂單
        if (addMeal && meals != null && !meals.isEmpty()){
            restoFacade.createBatchFromRoom(saved, roomOList, highChairs, req, meals);
        }
        return saved;  // Controller做後續處理
    }
}
	


