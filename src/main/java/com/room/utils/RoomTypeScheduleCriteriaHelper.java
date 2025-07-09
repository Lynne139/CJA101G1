package com.room.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.roomtypeschedule.model.RoomTypeScheduleVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RoomTypeScheduleCriteriaHelper {
	// 根據欄位類型自動組條件
	private static Predicate buildPredicate(CriteriaBuilder cb, Root<RoomTypeScheduleVO> root, String column,
			String value) {
		// 查詢條件沒有輸入資料，不要產生查詢條件
		if (value == null || value.trim().isEmpty())
			return null;
		// 和input的name相同
		switch (column) {
		case "roomTypeId":
			return cb.equal(root.get("roomTypeVO").get("roomTypeId"), Integer.valueOf(value));
		case "minDate":
			return cb.greaterThanOrEqualTo(root.get("roomOrderDate"), java.sql.Date.valueOf(value));
		case "maxDate":
			return cb.lessThanOrEqualTo(root.get("roomOrderDate"), java.sql.Date.valueOf(value));
		// cb.diff(a, b) 就是建立 (a - b) 的條件式
		case "minAmount":
		    return cb.ge(cb.diff(root.get("roomAmount"), root.get("roomRSVBooked")), Integer.valueOf(value));
		case "maxAmount":
		    return cb.le(cb.diff(root.get("roomAmount"), root.get("roomRSVBooked")), Integer.valueOf(value));
		// 沒有特別寫的case，做模糊查詢
		default:
				return null;
		}
	}

	// 主查詢方法
	public static List<RoomTypeScheduleVO> getRoomTypeScheduleCriteria(Map<String, String[]> map, EntityManager em) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<RoomTypeScheduleVO> cq = cb.createQuery(RoomTypeScheduleVO.class);
		Root<RoomTypeScheduleVO> root = cq.from(RoomTypeScheduleVO.class);

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
			    cb.asc(root.get("roomOrderDate")),
			    cb.asc(root.get("roomTypeVO").get("roomTypeId"))
			);

		return em.createQuery(cq).getResultList();
	}
}
