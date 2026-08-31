package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Enseignant;
import com.mycampus.backend.service.EnseignantService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class EnseignantController {

    private final EnseignantService enseignantService;

    public EnseignantController(EnseignantService enseignantService) {
        this.enseignantService = enseignantService;
    }

    // Consultable par tous les connectés (étudiants voient qui enseigne quoi)
    @GetMapping("/enseignants")
    public ResponseEntity<List<Enseignant>> getTous() {
        return ResponseEntity.ok(enseignantService.getTous());
    }

    @GetMapping("/enseignants/{id}")
    public ResponseEntity<Enseignant> getParId(@PathVariable Long id) {
        return ResponseEntity.ok(enseignantService.getParId(id));
    }

    // CRUD réservé admin
    @PostMapping("/admin/enseignants")
    public ResponseEntity<Enseignant> creer(@RequestBody Enseignant enseignant) {
        return ResponseEntity.status(HttpStatus.CREATED).body(enseignantService.creer(enseignant));
    }

    @PutMapping("/admin/enseignants/{id}")
    public ResponseEntity<Enseignant> modifier(@PathVariable Long id, @RequestBody Enseignant enseignant) {
        return ResponseEntity.ok(enseignantService.modifier(id, enseignant));
    }

    @DeleteMapping("/admin/enseignants/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        enseignantService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
