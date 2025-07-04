package com.notification.repository;

import com.notification.entity.Notification;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 根據會員編號查詢該會員的所有通知，新的排在前面
    List<Notification> findByMemberIdOrderByCreatedAtDesc(Integer memberId);
    
    // 根據會員編號查詢未讀通知數量
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.memberId = :memberId AND n.isRead = false")
    long countUnreadByMemberId(@Param("memberId") Integer memberId);

    // 根據通知ID和 memberId 查詢（用於權限驗證、更新通知為已讀狀態等情境）
    Notification findByNotificationIdAndMemberId(Integer notificationId, Integer memberId);

}
