package com.mycampus.backend.controller;

import com.mycampus.backend.entity.Note;
import com.mycampus.backend.service.NoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/etudiants/{etudiantId}/notes")
    public ResponseEntity<List<Note>> getNotesEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(noteService.getNotesEtudiant(etudiantId));
    }

    // Utilisé par le tableau de bord étudiant : moyenne + statut admis/ajourné
    @GetMapping("/etudiants/{etudiantId}/moyenne")
    public ResponseEntity<Map<String, Object>> getMoyenne(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(noteService.calculerMoyenneEtStatut(etudiantId));
    }

    @PostMapping("/admin/notes")
    public ResponseEntity<Note> ajouter(@RequestBody Note note) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.ajouterNote(note));
    }

    @PutMapping("/admin/notes/{id}")
    public ResponseEntity<Note> modifier(@PathVariable Long id, @RequestBody Note note) {
        return ResponseEntity.ok(noteService.modifierNote(id, note));
    }

    @DeleteMapping("/admin/notes/{id}")
    public ResponseEntity<Void> supprimer(@PathVariable Long id) {
        noteService.supprimerNote(id);
        return ResponseEntity.noContent().build();
    }
}
