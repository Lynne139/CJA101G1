package com.notification.repository;

import com.notification.entity.Notification;
import com.member.model.MemberVO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    // 根據會員查詢該會員的所有通知（可搭配排序）
    List<Notification> findByMember(MemberVO member);

    // 根據會員查詢未讀通知
//    List<Notification> findByMemberAndIsReadFalse(Member member);

    // 根據會員查詢已讀通知
//    List<Notification> findByMemberAndIsReadTrue(Member member);

    // 根據會員與通知ID查詢（用於權限驗證等情境）
//    Notification findByNotificationIdAndMember(Integer notificationId, Member member);

}
