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
                return cb.equal(root.get(column), Integer.valueOf(value));
            case "keyword":
                // ✅ 關鍵字查詢多欄位 OR
                String pattern = "%" + value + "%";
                 Predicate nameLike = cb.like(root.get("memberName"), pattern);
                // Predicate emailLike = cb.like(root.get("memberEmail"), pattern);
                Predicate orderLike = cb.like(root.get("roomOrderId"), pattern);
                Predicate midLike = cb.like(root.get("memberId"), pattern);
                return cb.or(orderLike, midLike);
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
//         