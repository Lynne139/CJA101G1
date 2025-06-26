package com.room.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.room.model.RoomVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RoomCriteriaHelper {
	// 根據欄位類型自動組條件
		private static Predicate buildPredicate(CriteriaBuilder cb, Root<RoomVO> root, String column, String value) {
			//查詢條件沒有輸入資料，不要產生查詢條件
		    if (value == null || value.trim().isEmpty()) return null;
		    //和input的name相同
		    switch (column) {
		        case "roomId":
		            return cb.equal(root.get(column), Integer.valueOf(value));
		        case "roomTypeId":
		        	return cb.equal(root.get("roomTypeVO").get("roomTypeId"), Integer.valueOf(value));
		        case "roomStatus":
		        	return cb.equal(root.get(column), Byte.valueOf(value));
		        case "roomSaleStatus":
		            return cb.equal(root.get(column), Byte.valueOf(value));
		        case "roomGuestName":
		            // ✅ 住客姓名查詢多欄位 OR
		            String pattern = "%" + value + "%";
		            Predicate nameLike = cb.like(root.get("roomGuestName"), pattern);
		            return cb.or(nameLike);
		        //沒有特別寫的case，做模糊查詢
		        default:
		            return cb.like(root.get(column), "%" + value + "%");
		    }
		}


	    // 主查詢方法
	    public static List<RoomVO> getRoomCriteria(Map<String, String[]> map, EntityManager em) {
	        CriteriaBuilder cb = em.getCriteriaBuilder();
	        CriteriaQuery<RoomVO> cq = cb.createQuery(RoomVO.class);
	        Root<RoomVO> root = cq.from(RoomVO.class);

	        List<Predicate> predicateList = new ArrayList<>();

	        // 處理前端傳來的 map 條件
	        for (String column : map.keySet()) {
	            if ("action".equals(column)) continue; // 忽略表單多餘欄位
	            String[] values = map.get(column);
	            //避免 map.get(column)[0] NullPointerException
	            String value = (values != null && values.length > 0) ? values[0] : null;
	            Predicate p = buildPredicate(cb, root, column, value);
	            if (p != null) {
	                predicateList.add(p);
	            }
	        }


	        // 組裝查詢條件，都設進where
	        cq.where(predicateList.toArray(new Predicate[0]));
	        cq.orderBy(cb.asc(root.get("roomId"))); // 可以改為其他欄位排序

	        return em.createQuery(cq).getResultList();
	    }
}
