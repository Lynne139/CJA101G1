package com.prod.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;



@Service("prodService")
public class ProdService {

	@Autowired
	ProdRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addProd(ProdVO prodVO) {
		repository.save(prodVO);
	}

	public void updateProd(ProdVO prodVO) {
		repository.save(prodVO);
	}


	public ProdVO getOneProd(Integer productId) {
		Optional<ProdVO> optional = repository.findById(productId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ProdVO> getAll() {
		return repository.findAll();
	}

//	public List<ProdVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_Emp3.getAllC(map,sessionFactory.openSession());
//	}

}