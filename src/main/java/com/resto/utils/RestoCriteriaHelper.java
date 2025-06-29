package com.resto.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.resto.dto.RestoDTO;
import com.resto.entity.RestoVO;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class RestoCriteriaHelper {

    // 根據欄位類型自動組條件
	private static Predicate buildPredicate(CriteriaBuilder cb, Root<RestoVO> root, String column, String value) {
	    if (value == null || value.trim().isEmpty()) return null;

	    switch (column) {
//	        case "restoSeatsTotal":
//	            return cb.equal(root.get(column), Integer.valueOf(value));
	        case "isEnabled":
	            return cb.equal(root.get(column), Boolean.valueOf(value));
	        case "keyword":
	            // 關鍵字查詢多欄位 OR
	            String pattern = "%" + value + "%";
	            Predicate nameLike = cb.like(root.get("restoName"), pattern);
	            Predicate nameEnLike = cb.like(root.get("restoNameEn"), pattern);
	            Predicate locLike = cb.like(root.get("restoLoc"), pattern);
	            return cb.or(nameLike, nameEnLike, locLike);
	        default:
	            return cb.like(root.get(column), "%" + value + "%");
	    }
	}


    // 主查詢方法
//    public static List<RestoVO> getAll(Map<String, String[]> map, EntityManager em) {
//        CriteriaBuilder cb = em.getCriteriaBuilder();
//        CriteriaQuery<RestoVO> cq = cb.createQuery(RestoVO.class);
//        Root<RestoVO> root = cq.from(RestoVO.class);
//
//        List<Predicate> predicateList = new ArrayList<>();
//
//        // 處理前端傳來的 map 條件
//        for (String column : map.keySet()) {
//            if ("action".equals(column)) continue; // 忽略表單多餘欄位
//            String value = map.get(column)[0];
//            Predicate p = buildPredicate(cb, root, column, value);
//            if (p != null) {
//                predicateList.add(p);
//            }
//        }
//
//        // 強制加入：isDeleted = false（避免軟刪除資料也抓到）
//        predicateList.add(cb.equal(root.get("isDeleted"), false));
//
//        // 組裝查詢條件，都設進where
//        cq.where(predicateList.toArray(new Predicate[0]));
//        cq.orderBy(cb.asc(root.get("restoId"))); // 可以改為其他欄位排序
//
//        return em.createQuery(cq).getResultList();
//    }
    
	public static List<RestoDTO> getAllDTO(Map<String, String[]> map, EntityManager em) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<RestoDTO> cq = cb.createQuery(RestoDTO.class);
        Root<RestoVO> root = cq.from(RestoVO.class);

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

        // 強制加入：isDeleted = false（避免軟刪除資料也抓到）
        predicateList.add(cb.equal(root.get("isDeleted"), false));
        
        // 使用 DTO 的 constructor 表達式選取欄位
        cq.select(cb.construct(RestoDTO.class,
            root.get("restoId"),
            root.get("restoName"),
            root.get("restoNameEn"),
            root.get("restoLoc"),
            root.get("restoSeatsTotal"),
            root.get("isEnabled")
        ));

        // 組裝查詢條件，都設進where
        if (!predicateList.isEmpty()) {
            cq.where(predicateList.toArray(new Predicate[0]));
        }
        cq.orderBy(cb.asc(root.get("restoId"))); // 可以改為其他欄位排序

        return em.createQuery(cq).getResultList();
    }
    
    
}
