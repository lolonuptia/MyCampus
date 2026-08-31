package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Cours;
import com.mycampus.backend.service.CoursService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CoursController {

    private final CoursService coursService;

    public CoursController(CoursService coursService) {
        this.coursService = coursService;
    }

    @GetMapping("/cours")
    public ResponseEntity<List<Cours>> getTous() {
        return ResponseEntity.ok(coursService.getTous());
    }

    @GetMapping("/cours/{id}")
    public ResponseEntity<Cours> getParId(@PathVariable Long id) {
        return ResponseEntity.ok(coursService.getParId(id));
    }

    // Fonctionnalité "Recherche" du cahier des charges
    @GetMapping("/cours/recherche")
    public ResponseEntity<List<Cours>> rechercher(@RequestParam String motCle) {
        return ResponseEntity.ok(coursService.rechercher(motCle));
    }

    @PostMapping("/admin/cours")
    public ResponseEntity<Cours> creer(@RequestBody Cours cours) {
        return ResponseEntity.status(HttpStatus.CREATED).body(coursService.creer(cours));
    }

    @PutMapping("/admin/cours/{id}")
    public ResponseEntity<Cours> modifier(@PathVariable Long id, @RequestBody Cours cours) {
        return ResponseEntity.ok(coursService.modifier(id, cours));
    }

    @DeleteMapping("/admin/cours/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        coursService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
