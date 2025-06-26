package com.news.repository;

import com.news.entity.PromotionNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PromotionNewsRepository extends JpaRepository<PromotionNews, Integer> {
    List<PromotionNews> findByIsDisplayTrue();
} 