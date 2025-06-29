package com.resto.model;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.resto.dto.RestoOrderDTO;
import com.resto.entity.RestoVO;
import com.resto.utils.RestoOrderCriteriaHelper;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class RestoOrderService {

    @PersistenceContext
    private EntityManager em;

    private final RestoOrderRepository restoOrderRepository;

    public RestoOrderService(RestoOrderRepository restoOrderRepository) {
        this.restoOrderRepository = restoOrderRepository;
    }

    // Datatable 預設載入
    @Transactional(readOnly = true)
    public List<RestoOrderDTO> findAllDTO() {
        return restoOrderRepository.findAllDTO();
    }

    // 複合查詢，把paramMap丟給CriteriaHelper
    @Transactional(readOnly = true)
    public List<RestoOrderDTO> compositeQueryAsDTO(Map<String,String[]> paramMap) {
        return RestoOrderCriteriaHelper.getAllDTO(paramMap, em);
    }
    
    
    
    
    
    
}
