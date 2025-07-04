package com.news.repository;

import com.news.entity.PromotionNews;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionNewsRepository extends JpaRepository<PromotionNews, Integer> {
    List<PromotionNews> findByIsDisplayTrue();
    Optional<PromotionNews> findFirstByIsDisplayTrueOrderByStartDateDesc();
} 