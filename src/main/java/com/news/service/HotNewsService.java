package com.news.service;

import com.news.entity.HotNews;
import com.news.repository.HotNewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HotNewsService {
    @Autowired
    private HotNewsRepository repo;

    public List<HotNews> findAll() {
        return repo.findAll();
    }

    public Optional<HotNews> findById(Integer id) {
        return repo.findById(id);
    }

    public HotNews save(HotNews news) {
        return repo.save(news);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    public List<HotNews> findByIsDisplayTrue() {
        return repo.findByIsDisplayTrue();
    }

    public Optional<HotNews> findLatestDisplay() {
        return repo.findFirstByIsDisplayTrueOrderByCreatedDateDesc();
    }
}
