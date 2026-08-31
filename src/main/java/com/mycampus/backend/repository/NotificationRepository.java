package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByEtudiantIdOrderByDateNotifDesc(Long etudiantId);
    List<Notification> findByEtudiantIdAndLuFalse(Long etudiantId);
}
