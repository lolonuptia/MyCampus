package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Annonce;
import com.mycampus.backend.service.AnnonceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AnnonceController {

    private final AnnonceService annonceService;

    public AnnonceController(AnnonceService annonceService) {
        this.annonceService = annonceService;
    }

    @GetMapping("/annonces")
    public ResponseEntity<List<Annonce>> getToutes() {
        return ResponseEntity.ok(annonceService.getToutes());
    }

    @GetMapping("/annonces/recherche")
    public ResponseEntity<List<Annonce>> rechercher(@RequestParam String motCle) {
        return ResponseEntity.ok(annonceService.rechercher(motCle));
    }

    @PostMapping("/admin/annonces")
    public ResponseEntity<Annonce> creer(@RequestBody Annonce annonce) {
        return ResponseEntity.status(HttpStatus.CREATED).body(annonceService.creer(annonce));
    }

    @PutMapping("/admin/annonces/{id}")
    public ResponseEntity<Annonce> modifier(@PathVariable Long id, @RequestBody Annonce annonce) {
        return ResponseEntity.ok(annonceService.modifier(id, annonce));
    }

    @DeleteMapping("/admin/annonces/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        annonceService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
