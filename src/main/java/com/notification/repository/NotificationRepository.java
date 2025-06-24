package com.notification.repository;

import com.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 根據會員編號查詢該會員的所有通知
    List<Notification> findByMemberId(Integer memberId);
    
    // 根據 memberId 查詢未讀通知
//    List<Notification> findByMemberIdAndIsReadFalse(Integer memberId);

    // 根據 memberId 查詢已讀通知
//    List<Notification> findByMemberIdAndIsReadTrue(Integer memberId);

    // 根據通知ID和 memberId 查詢（用於權限驗證等情境）
    Notification findByNotificationIdAndMemberId(Integer notificationId, Integer memberId);

}
