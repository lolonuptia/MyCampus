package com.mycampus.backend.service;

import com.mycampus.backend.entity.Notification;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.NotificationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> getParEtudiant(Long etudiantId) {
        return notificationRepository.findByEtudiantIdOrderByDateNotifDesc(etudiantId);
    }

    public List<Notification> getNonLues(Long etudiantId) {
        return notificationRepository.findByEtudiantIdAndLuFalse(etudiantId);
    }

    public Notification marquerCommeLue(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification introuvable avec l'id " + id));
        notification.setLu(true);
        return notificationRepository.save(notification);
    }
}
