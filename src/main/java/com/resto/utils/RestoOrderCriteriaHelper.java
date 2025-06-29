package com.resto.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.resto.dto.RestoOrderDTO;
import com.resto.entity.RestoOrderVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

/** 依照 paramMap 自動組裝複合查詢並回傳 DTO 清單 */
public class RestoOrderCriteriaHelper {

    private static Predicate buildPredicate(CriteriaBuilder cb, Root<RestoOrderVO> root,
                                            String column, String value) {
        if (value == null || value.isBlank()) return null;

        switch (column) {
            case "restoOrderId": // keyIn
            	return cb.equal(root.get(column), Integer.valueOf(value));
            case "memberId": // keyIn
                return cb.equal(root.get("memberVO").get("memberId"), Integer.valueOf(value));
            case "roomOrderId": //keyIn
                return cb.equal(root.get("roomOrder").get("roomOrderId"), Integer.valueOf(value));
	            
            case "orderStatus": // select
            	return cb.equal(root.get(column), RestoOrderStatus.valueOf(value));
            case "orderSource": // select
            	return cb.equal(root.get(column), RestoOrderSource.valueOf(value));
            	
            case "regiDateFrom":
                return cb.greaterThanOrEqualTo(root.get("regiDate"), LocalDate.parse(value));
            case "regiDateTo":
                return cb.lessThanOrEqualTo(root.get("regiDate"), LocalDate.parse(value));

            case "orderTimeFrom":           // 建議全部小寫，和表單欄位一致
                return cb.greaterThanOrEqualTo(root.get("orderTime"), LocalDateTime.parse(value));
            case "orderTimeTo":
                return cb.lessThanOrEqualTo(root.get("orderTime"), LocalDateTime.parse(value));



            case "hasAdminNote":
                return Boolean.parseBoolean(value)
                    ? cb.and(cb.isNotNull(root.get("adminNote")),
                             cb.notEqual(root.get("adminNote"), ""))
                    : null;
            case "hasRegiReq":
                return Boolean.parseBoolean(value)
                    ? cb.and(cb.isNotNull(root.get("regiReq")),
                             cb.notEqual(root.get("regiReq"), ""))
                    : null;
	            
            case "snapshotPeriodName": // keyIn
            case "snapshotTimeslotName": // keyIn
            case "snapshotRestoName": // keyIn
            	return cb.equal(root.get(column), value);        
            	
            case "keyword":  // keyIn
	            // 訂位者資料關鍵字查詢多欄位 OR
	            String pattern = "%" + value + "%";
	            Predicate guestNameLike = cb.like(root.get("orderGuestName"), pattern);
	            Predicate guestEmailLike = cb.like(root.get("orderGuestEmail"), pattern);
	            Predicate guestPhoneLike = cb.like(root.get("orderGuestPhone"), pattern);
	            return cb.or(guestNameLike, guestEmailLike, guestPhoneLike);
            default:
            	return null;   // 未涵蓋欄位忽略
        }
    }

    public static List<RestoOrderDTO> getAllDTO(Map<String,String[]> map, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RestoOrderDTO> cq = cb.createQuery(RestoOrderDTO.class);
        Root<RestoOrderVO> root = cq.from(RestoOrderVO.class);

        List<Predicate> predicateList = new ArrayList<>();

        // 處理前端傳來的 map 條件
        for (String column : map.keySet()) {
          String[] values = map.get(column);
          String value = (values != null && values.length > 0) ? values[0] : null;
        	
          Predicate p = buildPredicate(cb, root, column, value);
          if (p != null) {
              predicateList.add(p);
          }
      }

        // 使用 DTO 的 constructor 表達式選取欄位
        cq.select(cb.construct(RestoOrderDTO.class,
            root.get("restoOrderId"),
            root.get("snapshotRestoName"),
            root.get("orderSource"),
            root.get("memberVO").get("memberId"),
            root.get("roomOrder").get("roomOrderId"),
            root.get("orderGuestName"),
            root.get("orderGuestEmail"),
            root.get("orderGuestPhone"),
            root.get("regiDate"),
            root.get("snapshotPeriodName"),
            root.get("snapshotTimeslotName"),
            root.get("regiSeats"),
            root.get("orderTime"),
            root.get("orderStatus")
        ));

        // 組裝查詢條件，都設進where
        if (!predicateList.isEmpty()) {
            cq.where(predicateList.toArray(new Predicate[0]));
        }
        cq.orderBy(cb.desc(root.get("restoOrderId")));

        return em.createQuery(cq).getResultList();
    }
}
