package com.mycampus.backend.service;

import com.mycampus.backend.entity.Enseignant;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.EnseignantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnseignantService {

    private final EnseignantRepository enseignantRepository;

    public EnseignantService(EnseignantRepository enseignantRepository) {
        this.enseignantRepository = enseignantRepository;
    }

    public List<Enseignant> getTous() {
        return enseignantRepository.findAll();
    }

    public Enseignant getParId(Long id) {
        return enseignantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Enseignant introuvable avec l'id " + id));
    }

    public Enseignant creer(Enseignant enseignant) {
        return enseignantRepository.save(enseignant);
    }

    public Enseignant modifier(Long id, Enseignant modifie) {
        Enseignant enseignant = getParId(id);
        enseignant.setNom(modifie.getNom());
        enseignant.setPrenom(modifie.getPrenom());
        enseignant.setEmail(modifie.getEmail());
        enseignant.setSpecialite(modifie.getSpecialite());
        return enseignantRepository.save(enseignant);
    }

    public void supprimer(Long id) {
        if (!enseignantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Enseignant introuvable avec l'id " + id);
        }
        enseignantRepository.deleteById(id);
    }
}
