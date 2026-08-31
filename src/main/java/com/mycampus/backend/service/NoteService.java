package com.mycampus.backend.service;

import com.mycampus.backend.entity.Etudiant;
import com.mycampus.backend.entity.Note;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.EtudiantRepository;
import com.mycampus.backend.repository.NoteRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoteService {

    private static final BigDecimal SEUIL_ADMISSION = new BigDecimal("10.00");

    private final NoteRepository noteRepository;
    private final EtudiantRepository etudiantRepository;

    public NoteService(NoteRepository noteRepository, EtudiantRepository etudiantRepository) {
        this.noteRepository = noteRepository;
        this.etudiantRepository = etudiantRepository;
    }

    public List<Note> getNotesEtudiant(Long etudiantId) {
        etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));
        return noteRepository.findByEtudiantId(etudiantId);
    }

    public Note ajouterNote(Note note) {
        return noteRepository.save(note);
    }

    public Note modifierNote(Long id, Note noteModifiee) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Note introuvable"));
        note.setValeur(noteModifiee.getValeur());
        note.setTypeEvaluation(noteModifiee.getTypeEvaluation());
        return noteRepository.save(note);
    }

    public void supprimerNote(Long id) {
        if (!noteRepository.existsById(id)) {
            throw new ResourceNotFoundException("Note introuvable");
        }
        noteRepository.deleteById(id);
    }

    // Calcule la moyenne générale + le statut admis/ajourné pour le tableau de bord
    public Map<String, Object> calculerMoyenneEtStatut(Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable"));

        List<Note> notes = noteRepository.findByEtudiantId(etudiantId);

        Map<String, Object> resultat = new HashMap<>();

        if (notes.isEmpty()) {
            resultat.put("moyenne", null);
            resultat.put("statut", "AUCUNE_NOTE");
            resultat.put("nombreNotes", 0);
            return resultat;
        }

        BigDecimal somme = notes.stream()
                .map(Note::getValeur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal moyenne = somme.divide(new BigDecimal(notes.size()), 2, RoundingMode.HALF_UP);
        String statut = moyenne.compareTo(SEUIL_ADMISSION) >= 0 ? "ADMIS" : "AJOURNE";

        resultat.put("moyenne", moyenne);
        resultat.put("statut", statut);
        resultat.put("nombreNotes", notes.size());
        return resultat;
    }
}
