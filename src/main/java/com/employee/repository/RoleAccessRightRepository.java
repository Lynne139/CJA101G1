package com.employee.repository;

import com.employee.entity.RoleAccessRight;
import com.employee.entity.RoleAccessRightId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAccessRightRepository extends JpaRepository<RoleAccessRight, RoleAccessRightId> {
} 