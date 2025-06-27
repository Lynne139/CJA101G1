package com.roomOrder.model;

import com.roomOrder.model.RoomOrder;
import com.roomOrder.utils.RoomoCriteriaHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class RoomOrderService {
    private final RoomOrderRepository repository;

    @PersistenceContext
    private EntityManager em; // 讓 Spring 自動注入 EntityManager

    public RoomOrderService(RoomOrderRepository repository) {
        this.repository = repository;
    }

    public List<RoomOrder> findAll() {
        return repository.findAll();
    }

    // public Optional<RoomOrder> findById(Long roomOrderId) {
    //     return repository.findById(roomOrderId);
    // }

    public RoomOrder save(RoomOrder order) {
        return repository.save(order);
    }

    public void deleteById(Integer roomOrderId) {
        repository.deleteById(roomOrderId);
    }

    // 複合查詢（Criteria 結構）
    public List<RoomOrder> compositeQuery(Map<String, String[]> map) {
        return RoomoCriteriaHelper.getAll(map, em);
    }

    public RoomOrder getById(Integer roomOrderId) {
        return repository.findByRoomOrderId(roomOrderId);
    }

    // 更新訂單資料
    public RoomOrder updateOrder(RoomOrder order) {
        return repository.save(order);
    }

    

}