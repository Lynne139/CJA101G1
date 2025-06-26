package com.news.service;

import com.news.entity.PromotionNews;
import com.news.repository.PromotionNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PromotionNewsService {
    @Autowired
    private PromotionNewsRepository repo;

    public List<PromotionNews> findAll() {
        return repo.findAll();
    }

    public Optional<PromotionNews> findById(Integer id) {
        return repo.findById(id);
    }

    public PromotionNews save(PromotionNews news) {
        return repo.save(news);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    public List<PromotionNews> findByIsDisplayTrue() {
        return repo.findByIsDisplayTrue();
    }
} 