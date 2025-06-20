package com.prodCate.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.prodCate.model.*;



@Service("prodCateService")
public class ProdCateService {
	
	@Autowired
	ProdCateRepository repository;
	
	@Autowired
    private SessionFactory sessionFactory;

	public void addProdCate(ProdCateVO prodCateVO) {
		repository.save(prodCateVO);
	}

	public void updateProdCate(ProdCateVO prodCateVO) {
		repository.save(prodCateVO);
	}


	public ProdCateVO getOneProdCate(Integer prodCateId) {
		Optional<ProdCateVO> optional = repository.findById(prodCateId);
//		return optional.get();
		return optional.orElse(null);  // public T orElse(T other) : 如果值存在就回傳其值，否則回傳other的值
	}

	public List<ProdCateVO> getAll() {
		return repository.findAll();
	}

//	public List<ProdCateVO> getAll(Map<String, String[]> map) {
//		return HibernateUtil_CompositeQuery_Emp3.getAllC(map,sessionFactory.openSession());
//	}

}
