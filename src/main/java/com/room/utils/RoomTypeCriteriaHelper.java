package com.room.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.roomtype.model.RoomTypeVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RoomTypeCriteriaHelper {
	// 根據欄位類型自動組條件
	private static Predicate buildPredicate(CriteriaBuilder cb, Root<RoomTypeVO> root, String column, String value) {
		// 查詢條件沒有輸入資料，不要產生查詢條件
		if (value == null || value.trim().isEmpty())
			return null;
		// 和input的name相同
		switch (column) {
		case "roomTypeId":
			return cb.equal(root.get(column), Integer.valueOf(value));
		case "roomTypeName":
		    return cb.equal(root.get("roomTypeId"), value);
		case "roomSaleStatus":
			return cb.equal(root.get(column), Byte.valueOf(value));
		case "minPrice":
			return cb.ge(root.get("roomTypePrice"), Integer.valueOf(value));// greater than or equal
		case "maxPrice":
			return cb.le(root.get("roomTypePrice"), Integer.valueOf(value));// less than or equal
		case "minAmount":
			return cb.ge(root.get("roomTypeAmount"), Integer.valueOf(value));
		case "maxAmount":
			return cb.le(root.get("roomTypeAmount"), Integer.valueOf(value));
		case "roomTypeContent":
			String pattern = "%" + value + "%";
			Predicate nameLike = cb.like(root.get("roomTypeContent"), pattern);
			return cb.or(nameLike);
		default:
            return null;
		}
	}

	// 主查詢方法
	public static List<RoomTypeVO> getRoomTypeCriteria(Map<String, String[]> map, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RoomTypeVO> cq = cb.createQuery(RoomTypeVO.class);
		Root<RoomTypeVO> root = cq.from(RoomTypeVO.class);

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
		cq.orderBy(cb.asc(root.get("roomTypeId"))); // 可以改為其他欄位排序

		return em.createQuery(cq).getResultList();
	}
}
