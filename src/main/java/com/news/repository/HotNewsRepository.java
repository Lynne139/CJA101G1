package com.news.repository;

import com.news.entity.HotNews;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotNewsRepository extends JpaRepository<HotNews, Integer> {
    List<HotNews> findByIsDisplayTrue();
    Optional<HotNews> findFirstByIsDisplayTrueOrderByCreatedDateDesc();
}
