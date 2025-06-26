package com.employee.repository;

import com.employee.entity.JobTitle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobTitleRepository extends JpaRepository<JobTitle, Integer> {
    // 可以在這裡添加自訂查詢方法
} 