package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Etudiant;
import com.mycampus.backend.service.EtudiantService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final EtudiantService etudiantService;

    public EtudiantController(EtudiantService etudiantService) {
        this.etudiantService = etudiantService;
    }

    // Réservé admin : liste tous les étudiants
    @GetMapping
    public ResponseEntity<List<Etudiant>> getTous() {
        return ResponseEntity.ok(etudiantService.getTous());
    }

    /** Fiche étudiant du compte JWT (à déclarer avant /{id}). */
    @GetMapping("/moi")
    public ResponseEntity<Etudiant> getMoi(Authentication authentication) {
        return ResponseEntity.ok(etudiantService.getParEmail(authentication.getName()));
    }

    // Étudiant connecté ou admin : voir un profil précis
    @GetMapping("/{id}")
    public ResponseEntity<Etudiant> getParId(@PathVariable Long id) {
        return ResponseEntity.ok(etudiantService.getParId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Etudiant> modifier(@PathVariable Long id, @RequestBody Etudiant etudiant) {
        return ResponseEntity.ok(etudiantService.modifier(id, etudiant));
    }

    // Réservé admin : suppression d'un étudiant
    @DeleteMapping("/admin/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        etudiantService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
