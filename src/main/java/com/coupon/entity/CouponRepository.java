package com.coupon.entity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.prod.model.ProdVO;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, String>{
	
	

}
