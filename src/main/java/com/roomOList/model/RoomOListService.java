package com.roomOList.model;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.roomOList.model.RoomOList;
import com.roomOList.model.RoomOListRepository;

@Service
public class RoomOListService {

    @Autowired
    private RoomOListRepository repository;

    // 根據訂單明細ID查詢
    public RoomOList findByRoomOrderListId(Integer roomOrderListId) {
        return repository.findByRoomOrderListId(roomOrderListId);
    }

    // 新增訂單明細
    public RoomOList save(RoomOList roomOList) {
        return repository.save(roomOList);
    }

    // 刪除訂單明細
    public void deleteByRoomOrderListId(Integer roomOrderListId) {  
        repository.deleteByRoomOrderListId(roomOrderListId);
    }

    // 查詢所有訂單明細
    public List<RoomOList> findAll() {
        return repository.findAll();
    }

}
