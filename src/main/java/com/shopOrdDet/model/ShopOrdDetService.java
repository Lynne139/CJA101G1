package com.shopOrdDet.model;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("shopOrdDetService")
public class ShopOrdDetService {
	
	@Autowired
    private ShopOrdDetRepository shopOrdDetRepository;
	
	@Autowired
    SessionFactory sessionFactory;

    // 新增或更新明細
    @Transactional
    public void addOrUpdateShopOrdDet(ShopOrdDetVO detail) {
        if (detail.getPpid() == null) {
            ShopOrdDetIdVO id = new ShopOrdDetIdVO();
            if (detail.getShopOrdVO() != null) {
                id.setProdOrdId(detail.getShopOrdVO().getProdOrdId());
            }
            if (detail.getProdVO() != null) {
                id.setProductId(detail.getProdVO().getProductId());
            }
            detail.setPpid(id);
        }
        shopOrdDetRepository.save(detail);
    }

    // 查詢單一明細
    public ShopOrdDetVO getOneShopOrdDet(Integer prodOrdId, Integer productId) {
        return shopOrdDetRepository.findByPpid_ProdOrdIdAndPpid_ProductId(prodOrdId, productId).orElse(null);
    }

    // 查詢某訂單所有明細
    public List<ShopOrdDetVO> getShopOrdDetByOrderId(Integer prodOrdId) {
        return shopOrdDetRepository.findByShopOrdVO_ProdOrdId(prodOrdId);
    }

    // 刪除單一明細
    @Transactional
    public void deleteShopOrdDet(Integer prodOrdId, Integer productId) {
        shopOrdDetRepository.deleteByPpid_ProdOrdIdAndPpid_ProductId(prodOrdId, productId);
    }

    // 查詢全部
    public List<ShopOrdDetVO> getAll() {
        return shopOrdDetRepository.findAll();
    }

}
