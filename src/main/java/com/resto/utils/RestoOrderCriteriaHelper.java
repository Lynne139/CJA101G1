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

public class RestoOrderCriteriaHelper {

//    private static Predicate buildPredicate(CriteriaBuilder cb, Root<RestoOrderVO> root,
//                                            String column, String value) {
//        if (value == null || value.isBlank()) return null;
//
//        switch (column) {
//            case "restoOrderId": // keyIn
//            	return cb.equal(root.get(column), Integer.valueOf(value));
//            case "memberId": // keyIn
//                return cb.equal(root.get("memberVO").get("memberId"), Integer.valueOf(value));
//            case "roomOrderId": //keyIn
//                return cb.equal(root.get("roomOrder").get("roomOrderId"), Integer.valueOf(value));
//	            
//            case "orderStatus": // select
//            	return cb.equal(root.get(column), RestoOrderStatus.valueOf(value));
//            case "orderSource": // select
//            	return cb.equal(root.get(column), RestoOrderSource.valueOf(value));
//            	
//            case "regiDateRange": // pick
//                String[] parts = value.split(" to ");
//                if (parts.length == 2) {
//                    Predicate from = cb.greaterThanOrEqualTo(root.get("regiDate"), LocalDate.parse(parts[0]));
//                    Predicate to = cb.lessThanOrEqualTo(root.get("regiDate"), LocalDate.parse(parts[1]));
//                    return cb.and(from, to);
//                }
//                return null;
//                
//            case "orderTimeRange":
//                String[] timeParts = value.split(" to ");
//                if (timeParts.length == 2) {
//                    Predicate from = cb.greaterThanOrEqualTo(root.get("orderTime"), LocalDateTime.parse(timeParts[0]));
//                    Predicate to = cb.lessThanOrEqualTo(root.get("orderTime"), LocalDateTime.parse(timeParts[1]));
//                    return cb.and(from, to);
//                }
//                return null;
//
//
//            case "hasAdminNote":  // checkbox
//                return Boolean.parseBoolean(value)
//                    ? cb.and(cb.isNotNull(root.get("adminNote")),
//                             cb.notEqual(root.get("adminNote"), ""))
//                    : null;
//            case "hasRegiReq":  // checkbox
//                return Boolean.parseBoolean(value)
//                    ? cb.and(cb.isNotNull(root.get("regiReq")),
//                             cb.notEqual(root.get("regiReq"), ""))
//                    : null;
//	            
//            case "snapshotPeriodName": // keyIn
//            case "snapshotTimeslotName": // keyIn
//            case "snapshotRestoName": // keyIn
//            	return cb.equal(root.get(column), value);        
//            	
//            case "keyword":  // keyIn
//	            // 訂位者資料關鍵字查詢多欄位 OR
//	            String pattern = "%" + value + "%";
//	            Predicate guestNameLike = cb.like(root.get("orderGuestName"), pattern);
//	            Predicate guestEmailLike = cb.like(root.get("orderGuestEmail"), pattern);
//	            return cb.or(guestNameLike, guestEmailLike);
//            default:
//            	return null;   // 未涵蓋欄位忽略
//        }
//    }
	
	private static Predicate buildPredicate(CriteriaBuilder cb, Root<RestoOrderVO> root,
            String column, String value) {
		if (value == null || value.isBlank()) return null;
		
		try {
		switch (column) {
		
		// ---------- 整數 ----------
		case "restoOrderId":
		case "memberId":
		case "roomOrderId": {
		Integer id = ParamUtils.parseIntSafe(new String[]{value});
		if (id == null) return cb.disjunction();
		return cb.equal(
		column.equals("memberId")   ? root.get("memberVO").get("memberId") :
		column.equals("roomOrderId")? root.get("roomOrder").get("roomOrderId") :
		                      root.get(column),
		id
		);
		}
		
		// ---------- Enum ----------
		case "orderStatus": {
		RestoOrderStatus st = ParamUtils.parseEnumSafe(new String[]{value}, RestoOrderStatus.class);
		return (st == null) ? cb.disjunction() : cb.equal(root.get(column), st);
		}
		case "orderSource": {
		RestoOrderSource src = ParamUtils.parseEnumSafe(new String[]{value}, RestoOrderSource.class);
		return (src == null) ? cb.disjunction() : cb.equal(root.get(column), src);
		}
		
		// ---------- 日期區間 ----------
		case "regiDateRange": {
			String[] parts = value.split(" to ");
			if (parts.length != 2) return cb.disjunction();
			LocalDate from = ParamUtils.parseLocalDateSafe(new String[]{parts[0]});
			LocalDate to   = ParamUtils.parseLocalDateSafe(new String[]{parts[1]});
			return (from == null || to == null)
			? cb.disjunction()
			: cb.and(
			cb.greaterThanOrEqualTo(root.get("regiDate"), from),
			cb.lessThanOrEqualTo(root.get("regiDate"), to)
			);
		}
		
		case "orderTimeRange": {
			String[] parts = value.split(" to ");
			if (parts.length != 2) return cb.disjunction();
			LocalDateTime from = ParamUtils.parseLocalDateTimeSafe(new String[]{parts[0]});
			LocalDateTime to   = ParamUtils.parseLocalDateTimeSafe(new String[]{parts[1]});
			return (from == null || to == null)
			? cb.disjunction()
			: cb.and(
			cb.greaterThanOrEqualTo(root.get("orderTime"), from),
			cb.lessThanOrEqualTo(root.get("orderTime"), to)
			);
		}
		
		// ---------- Checkbox ----------
		case "hasAdminNote":
			return Boolean.parseBoolean(value)
			? cb.and(cb.isNotNull(root.get("adminNote")), cb.notEqual(root.get("adminNote"), ""))
			: null;
		
		case "hasRegiReq":
			return Boolean.parseBoolean(value)
			? cb.and(cb.isNotNull(root.get("regiReq")), cb.notEqual(root.get("regiReq"), ""))
			: null;
			
		// ---------- 文字精確匹配 ----------
		case "snapshotPeriodName":
		case "snapshotTimeslotName":
		case "snapshotRestoName":
			return cb.equal(root.get(column), value);
		
		// ---------- 關鍵字 like ----------
		case "keyword":
		String pattern = "%" + value + "%";
			return cb.or(
			cb.like(root.get("orderGuestName"), pattern),
			cb.like(root.get("orderGuestEmail"), pattern)
			);
		
		default:
			return null; // 忽略未支援欄位
		}
		
		} catch (Exception e) {
			return cb.disjunction(); // 防止個別欄位例外導致整體查詢失敗
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
