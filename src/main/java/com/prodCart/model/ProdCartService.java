package com.prodCart.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prod.model.ProdVO;

@Service("prodCartService")
public class ProdCartService {

    @Autowired
    ProdCartRepository prodCartRepository;
    
    @Autowired
    SessionFactory sessionFactory;
    
    public void addProdCart(ProdCartVO cart) {
    	// 確保複合主鍵被正確設置
    	if (cart.getPmid() == null) {
    		ProdMemberIdVO id = new ProdMemberIdVO();
    		if (cart.getProdVO() != null) {
    			id.setProductId(cart.getProdVO().getProductId());
    		}
    		if (cart.getMemberVO() != null) {
    			id.setMemberId(cart.getMemberVO().getMemberId());
    		}
    		cart.setPmid(id);
    	}
    	
    	// 檢查是否已存在相同的商品
    	Optional<ProdCartVO> existingCart = prodCartRepository.findByPmid_ProductIdAndPmid_MemberId(
    		cart.getPmid().getProductId(), cart.getPmid().getMemberId());
    	
    	if (existingCart.isPresent()) {
    		// 如果已存在，更新數量
    		ProdCartVO existing = existingCart.get();
    		existing.setQuantity(existing.getQuantity() + cart.getQuantity());
    		prodCartRepository.save(existing);
    	} else {
    		// 如果不存在，新增
    		prodCartRepository.save(cart);
    	}
	}

	public void updateProdCart(ProdCartVO cart) {
		// 確保複合主鍵被正確設置
		if (cart.getPmid() == null) {
			ProdMemberIdVO id = new ProdMemberIdVO();
			if (cart.getProdVO() != null) {
				id.setProductId(cart.getProdVO().getProductId());
			}
			if (cart.getMemberVO() != null) {
				id.setMemberId(cart.getMemberVO().getMemberId());
			}
			cart.setPmid(id);
		}
		
		prodCartRepository.save(cart);
	}
	
	// 刪除購物車中一項商品
    public void deleteProdCart(Integer productId, Integer memberId) {
        prodCartRepository.deleteByPmid_ProductIdAndPmid_MemberId(productId, memberId);
    }
	
//	gpt幫我寫的 上面是我參考寫的
//	public ProdCartVO saveOrUpdate(ProdCartVO cart) {
//        return prodCartRepository.save(cart);
//    }


    // 查詢購物車中指定商品==listOne
    public ProdCartVO getOneProdCart(Integer productId, Integer memberId) {
        return prodCartRepository.findByPmid_ProductIdAndPmid_MemberId(productId, memberId).orElse(null);
    }
    
    // 查詢會員購物車所有商品==listAllByMemberId
    public List<ProdCartVO> getProdCartByMemberId(Integer memberId) {
        return prodCartRepository.findByMemberVO_MemberId(memberId);
    }
    
    //資料庫裡的全部顯示
    public List<ProdCartVO> getAll() {
		return prodCartRepository.findAll();
	}
    
    
}