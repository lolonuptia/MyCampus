package com.mycampus.backend.service;

import com.mycampus.backend.entity.Seance;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.SeanceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeanceService {

    private final SeanceRepository seanceRepository;

    public SeanceService(SeanceRepository seanceRepository) {
        this.seanceRepository = seanceRepository;
    }

    public List<Seance> getTous() {
        return seanceRepository.findAll();
    }

    public List<Seance> getParCours(Long coursId) {
        return seanceRepository.findByCoursId(coursId);
    }

    public Seance getParId(Long id) {
        return seanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Séance introuvable avec l'id " + id));
    }

    public Seance creer(Seance seance) {
        return seanceRepository.save(seance);
    }

    public Seance modifier(Long id, Seance modifiee) {
        Seance seance = getParId(id);
        seance.setCours(modifiee.getCours());
        seance.setDateSeance(modifiee.getDateSeance());
        seance.setHeureDebut(modifiee.getHeureDebut());
        seance.setHeureFin(modifiee.getHeureFin());
        seance.setSalle(modifiee.getSalle());
        return seanceRepository.save(seance);
    }

    public void supprimer(Long id) {
        if (!seanceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Séance introuvable avec l'id " + id);
        }
        seanceRepository.deleteById(id);
    }
}
