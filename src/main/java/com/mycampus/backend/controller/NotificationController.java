package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Notification;
import com.mycampus.backend.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants/{etudiantId}/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getToutes(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(notificationService.getParEtudiant(etudiantId));
    }

    @GetMapping("/non-lues")
    public ResponseEntity<List<Notification>> getNonLues(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(notificationService.getNonLues(etudiantId));
    }

    @PatchMapping("/{id}/lue")
    public ResponseEntity<Notification> marquerCommeLue(@PathVariable Long etudiantId, @PathVariable Long id) {
        return ResponseEntity.ok(notificationService.marquerCommeLue(id));
    }
}
