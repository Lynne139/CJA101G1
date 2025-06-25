package com.roomOrder.utils;

import com.roomOrder.model.RoomOrder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RoomoCriteriaHelper {
    // 根據欄位類型自動組條件
    private static Predicate buildPredicate(CriteriaBuilder cb, Root<RoomOrder> root, String column, String value) {
        if (value == null || value.trim().isEmpty()) return null;

        switch (column) {
            case "isEnabled":
                // 改成正確的欄位名稱
                return cb.equal(root.get("orderStatus"), Integer.valueOf(value));
            case "keyword":
                String pattern = "%" + value + "%";
                List<Predicate> orList = new ArrayList<>();
                // 會員姓名模糊查詢
                orList.add(cb.like(root.get("member").get("memberName"), pattern));
                // 訂單編號精確查詢（若輸入為數字）
                try {
                    Integer orderId = Integer.valueOf(value);
                    orList.add(cb.equal(root.get("roomOrderId"), orderId));
                } catch (NumberFormatException ignored) {}
                // 會員ID精確查詢（若輸入為數字）
                try {
                    Integer memberId = Integer.valueOf(value);
                    orList.add(cb.equal(root.get("member").get("memberId"), memberId));
                } catch (NumberFormatException ignored) {}
                return cb.or(orList.toArray(new Predicate[0]));
            default:
                return cb.like(root.get(column), "%" + value + "%");
        }
    }


    // 主查詢方法
    public static List<RoomOrder> getAll(Map<String, String[]> map, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RoomOrder> cq = cb.createQuery(RoomOrder.class);
        Root<RoomOrder> root = cq.from(RoomOrder.class);

        List<Predicate> predicateList = new ArrayList<>();

        // 處理前端傳來的 map 條件
        for (String column : map.keySet()) {
            if ("action".equals(column)) continue; // 忽略表單多餘欄位
            String value = map.get(column)[0];
            Predicate p = buildPredicate(cb, root, column, value);
            if (p != null) {
                predicateList.add(p);
            }
        }

        // 組裝查詢條件，都設進where
        cq.where(predicateList.toArray(new Predicate[0]));
        cq.orderBy(cb.asc(root.get("roomOrderId"))); // 可以改為其他欄位排序

        return em.createQuery(cq).getResultList();
    }

}