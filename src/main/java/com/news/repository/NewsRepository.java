package com.news.repository;

import com.news.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

public interface NewsRepository extends JpaRepository<News, Integer> {
    List<News> findByIsDisplayTrue();
    Optional<News> findFirstByIsDisplayTrueOrderByPublishedDateDesc();
} 