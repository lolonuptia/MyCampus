package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Seance;
import com.mycampus.backend.service.SeanceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class SeanceController {

    private final SeanceService seanceService;

    public SeanceController(SeanceService seanceService) {
        this.seanceService = seanceService;
    }

    @GetMapping("/seances")
    public ResponseEntity<List<Seance>> getToutes() {
        return ResponseEntity.ok(seanceService.getTous());
    }

    // Emploi du temps d'un cours précis
    @GetMapping("/cours/{coursId}/seances")
    public ResponseEntity<List<Seance>> getParCours(@PathVariable Long coursId) {
        return ResponseEntity.ok(seanceService.getParCours(coursId));
    }

    @PostMapping("/admin/seances")
    public ResponseEntity<Seance> creer(@RequestBody Seance seance) {
        return ResponseEntity.status(HttpStatus.CREATED).body(seanceService.creer(seance));
    }

    @PutMapping("/admin/seances/{id}")
    public ResponseEntity<Seance> modifier(@PathVariable Long id, @RequestBody Seance seance) {
        return ResponseEntity.ok(seanceService.modifier(id, seance));
    }

    @DeleteMapping("/admin/seances/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        seanceService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
