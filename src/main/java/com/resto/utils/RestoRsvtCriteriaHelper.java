package com.resto.utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.resto.entity.RestoReservationVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RestoRsvtCriteriaHelper {

    // 根據欄位類型自動組條件
	private static Predicate buildPredicate(CriteriaBuilder cb, Root<RestoReservationVO> root, String column, String value) {
	    if (value == null || value.trim().isEmpty()) return null;

	    switch (column) {
	        case "restoId":
	        	return cb.equal(root.get("restoVO").get("restoId"), Integer.valueOf(value));
	        case "minDate":
				return cb.greaterThanOrEqualTo(root.get("reserveDate"), java.sql.Date.valueOf(value));
			case "maxDate":
				return cb.lessThanOrEqualTo(root.get("reserveDate"), java.sql.Date.valueOf(value));
	        case "availableMin":
				int need = Integer.parseInt(value);   // value 來自使用者輸入
				
				//cb.diff(A, B) 讓 JPA 產生 (A - B)；cb.ge(expr, need) 產生 >= 條件
			    Expression<Integer> remain =
			            cb.diff(root.get("restoSeatsTotal"), root.get("reserveSeatsTotal"));
			    return cb.ge(remain, need);           // restoSeatsTotal - reserveSeatsTotal >= need
				       	
			default:
	            return cb.like(root.get(column), "%" + value + "%");
	    }
	}


    // 主查詢方法
    public static List<RestoReservationVO> getAll(Map<String, String[]> map, EntityManager em) {
    	CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RestoReservationVO> cq = cb.createQuery(RestoReservationVO.class);
        Root<RestoReservationVO> root = cq.from(RestoReservationVO.class);

        List<Predicate> predicateList = new ArrayList<>();

        // 處理前端傳來的 map 條件
        for (String column : map.keySet()) {
			if ("action".equals(column))
				continue; // 忽略表單多餘欄位
			String[] values = map.get(column);
			// 避免 map.get(column)[0] NullPointerException
			String value = (values != null && values.length > 0) ? values[0] : null;
			Predicate p = buildPredicate(cb, root, column, value);
			if (p != null) {
				predicateList.add(p);
			}
		}

        // 組裝查詢條件，都設進where
        cq.where(predicateList.toArray(new Predicate[0]));
        
    	    cq.orderBy(
    	    	    cb.asc(root.get("reserveDate")),
    	    	    cb.asc(root.get("reserveTimeslotVO").get("timeslotName")),
    	    	    cb.asc(root.get("restoVO").get("restoId"))
    	    	);

        return em.createQuery(cq).getResultList();
    
    
    }
    
    
	
    
    
}