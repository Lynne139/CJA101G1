package com.coupon.entity;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prod.model.ProdRepository;
import com.prod.model.ProdVO;

@Service("CouponService")
public class CouponService {
	
	@Autowired
	CouponRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	public void addCoupon(Coupon coupon) {
		repository.save(coupon);
	}

	public void updateCoupon(Coupon coupon) {
		repository.save(coupon);
	}


	public Coupon getOneCoupon(String couponCode) {
		Optional<Coupon> optional = repository.findById(couponCode);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<Coupon> getAll() {
		return repository.findAll();
	}

}
