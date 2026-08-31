package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Absence;
import com.mycampus.backend.service.AbsenceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AbsenceController {

    private final AbsenceService absenceService;

    public AbsenceController(AbsenceService absenceService) {
        this.absenceService = absenceService;
    }

    @GetMapping("/etudiants/{etudiantId}/absences")
    public ResponseEntity<List<Absence>> getParEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(absenceService.getParEtudiant(etudiantId));
    }

    @PostMapping("/admin/absences")
    public ResponseEntity<Absence> creer(@RequestBody Absence absence) {
        return ResponseEntity.status(HttpStatus.CREATED).body(absenceService.creer(absence));
    }

    // Ex: l'admin justifie une absence après réception d'un certificat
    @PatchMapping("/admin/absences/{id}/statut")
    public ResponseEntity<Absence> modifierStatut(@PathVariable Long id,
                                                    @RequestParam String statut,
                                                    @RequestParam(required = false) String justificatif) {
        return ResponseEntity.ok(absenceService.modifierStatut(id, statut, justificatif));
    }

    @DeleteMapping("/admin/absences/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        absenceService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
