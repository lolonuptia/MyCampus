package com.mycampus.backend.service;

import com.mycampus.backend.entity.Cours;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.CoursRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CoursService {

    private final CoursRepository coursRepository;

    public CoursService(CoursRepository coursRepository) {
        this.coursRepository = coursRepository;
    }

    public List<Cours> getTous() {
        return coursRepository.findAll();
    }

    public Cours getParId(Long id) {
        return coursRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cours introuvable avec l'id " + id));
    }

    public List<Cours> rechercher(String motCle) {
        return coursRepository.findByTitreContainingIgnoreCase(motCle);
    }

    public Cours creer(Cours cours) {
        return coursRepository.save(cours);
    }

    public Cours modifier(Long id, Cours modifie) {
        Cours cours = getParId(id);
        cours.setTitre(modifie.getTitre());
        cours.setDescription(modifie.getDescription());
        cours.setEnseignant(modifie.getEnseignant());
        cours.setFiliere(modifie.getFiliere());
        return coursRepository.save(cours);
    }

    public void supprimer(Long id) {
        if (!coursRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cours introuvable avec l'id " + id);
        }
        coursRepository.deleteById(id);
    }
}
