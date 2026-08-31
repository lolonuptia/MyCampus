package com.mycampus.backend.service;

import com.mycampus.backend.entity.Absence;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.AbsenceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbsenceService {

    private final AbsenceRepository absenceRepository;

    public AbsenceService(AbsenceRepository absenceRepository) {
        this.absenceRepository = absenceRepository;
    }

    public List<Absence> getParEtudiant(Long etudiantId) {
        return absenceRepository.findByEtudiantId(etudiantId);
    }

    public Absence getParId(Long id) {
        return absenceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Absence introuvable avec l'id " + id));
    }

    public Absence creer(Absence absence) {
        return absenceRepository.save(absence);
    }

    public Absence modifierStatut(Long id, String statut, String justificatif) {
        Absence absence = getParId(id);
        absence.setStatut(statut);
        absence.setJustificatif(justificatif);
        return absenceRepository.save(absence);
    }

    public void supprimer(Long id) {
        if (!absenceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Absence introuvable avec l'id " + id);
        }
        absenceRepository.deleteById(id);
    }
}
