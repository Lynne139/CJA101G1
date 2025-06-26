package com.news.repository;

import com.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Integer> {
    List<News> findByIsDisplayTrue();
} 