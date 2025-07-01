package com.employee.repository;

import com.employee.entity.FunctionAccessRight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionAccessRightRepository extends JpaRepository<FunctionAccessRight, Integer> {
} 