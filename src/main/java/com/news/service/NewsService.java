package com.news.service;

import com.news.entity.News;
import com.news.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class NewsService {
    @Autowired
    private NewsRepository repo;

    public List<News> findAll() {
        return repo.findAll();
    }

    public Optional<News> findById(Integer id) {
        return repo.findById(id);
    }

    public News save(News news) {
        return repo.save(news);
    }

    public void deleteById(Integer id) {
        repo.deleteById(id);
    }

    public List<News> findByIsDisplayTrue() {
        return repo.findByIsDisplayTrue();
    }

    public Optional<News> findLatestDisplay() {
        return repo.findFirstByIsDisplayTrueOrderByPublishedDateDesc();
    }
} 