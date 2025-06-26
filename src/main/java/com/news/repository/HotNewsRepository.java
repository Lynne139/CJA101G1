package com.news.repository;

import com.news.entity.HotNews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotNewsRepository extends JpaRepository<HotNews, Integer> {
    List<HotNews> findByIsDisplayTrue();
}
