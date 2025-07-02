package com.resto.integration.room;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	
	
	// 同步新增
    @Transactional // 確保住房訂單與餐廳訂單綁定為同批交易(一起成功或rollback)
    public Map<String, Object> createRoomAndMeals(RoomOrder roomOrder,
    									RoomOList roomOList,
                                        int highChairs,
                                        String restoReq,
                                        List<RestoOrderFromRoomDTO> meals,
                                        boolean addMeal
        ){

    	Map<String, Object> result = new HashMap<>();
    	
    	// 先儲存房訂單
        RoomOrder savedRoom = roomSvc.save(roomOrder);
        result.put("roomOrder", savedRoom);

        // 需要加購才批次建立餐廳訂單，會回傳產生的所有餐廳訂單id
        if (addMeal && meals != null && !meals.isEmpty()) {
            List<Integer> restoOrderIds = restoFacade.createBatchFromRoom(
                savedRoom, roomOList, highChairs, restoReq, meals
            );
            result.put("restoOrderIds", restoOrderIds);
        } else {
        	// 無需加餐廳時返回空清單
            result.put("restoOrderIds", List.of());
        }
        return result;  // Controller看需求做後續處理
    }
    
    
    // 同步更新
    @Transactional
    public Map<String, Object> updateRoomAndMeals(RoomOrder roomOrder,
                                                  RoomOList roomOList,
                                                  int highChairs,
                                                  String restoReq,
                                                  List<RestoOrderFromRoomDTO> meals,
                                                  boolean addMeal
    ) {
        Map<String, Object> result = new HashMap<>();

        // 更新住宿訂單（房號、日期、備註、狀態等欄位）
        RoomOrder updatedRoom = roomSvc.updateOrder(roomOrder);
        result.put("roomOrder", updatedRoom);

        // 如果有加購餐廳
        if (addMeal && meals != null && !meals.isEmpty()) {
            List<Integer> updatedRestoIds = restoFacade.updateBatchFromRoom(
                updatedRoom, roomOList, highChairs, restoReq, meals
            );
            result.put("restoOrderIds", updatedRestoIds);
        } else {
        	// 沒選餐廳就不動原有資料
            result.put("restoOrderIds", List.of());
        }

        return result;
    }

    
    
    
    
    
    
}
	


