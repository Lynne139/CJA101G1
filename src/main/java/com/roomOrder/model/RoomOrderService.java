package com.roomOrder.model;

import com.roomOrder.model.RoomOrder;
import com.roomOrder.utils.RoomoCriteriaHelper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import com.roomOList.model.RoomOList;

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

    // 計算專案加購總金額（專案價格 × 專案人數）
    public int calculateProjectTotal(RoomOrder order) {
        if (order == null || order.getOrderDetails() == null) return 0;
        int projectPrice = 0;
        int people = 0;
        // 依照專案方案決定價格
        // 這裡假設 order 物件有 projectPlan 屬性（若無請補充），或用其他方式取得方案價格
        // 範例：1:800, 2:1800, 3:2800
        Integer plan = null;
        try {
            plan = (Integer) order.getClass().getMethod("getProjectPlan").invoke(order);
        } catch (Exception e) {
            plan = null;
        }
        if (plan != null) {
            if (plan == 1) projectPrice = 800;
            else if (plan == 2) projectPrice = 1800;
            else if (plan == 3) projectPrice = 2800;
        }
        // 專案人數 = 所有明細入住人數總和
        people = order.getOrderDetails().stream().mapToInt(od -> od.getNumberOfPeople() != null ? od.getNumberOfPeople() : 0).sum();
        return projectPrice * people;
    }

    public List<RoomOrder> getByMemberId(Integer memberId) {
        return repository.getByMember_MemberId(memberId);
    }

}