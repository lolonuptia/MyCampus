package com.mycampus.backend.service;

import com.mycampus.backend.entity.Annonce;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.AnnonceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnnonceService {

    private final AnnonceRepository annonceRepository;

    public AnnonceService(AnnonceRepository annonceRepository) {
        this.annonceRepository = annonceRepository;
    }

    public List<Annonce> getToutes() {
        return annonceRepository.findAllByOrderByDatePublicationDesc();
    }

    public List<Annonce> rechercher(String motCle) {
        return annonceRepository.findByTitreContainingIgnoreCase(motCle);
    }

    public Annonce getParId(Long id) {
        return annonceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable avec l'id " + id));
    }

    public Annonce creer(Annonce annonce) {
        return annonceRepository.save(annonce);
    }

    public Annonce modifier(Long id, Annonce modifiee) {
        Annonce annonce = getParId(id);
        annonce.setTitre(modifiee.getTitre());
        annonce.setContenu(modifiee.getContenu());
        annonce.setImportante(modifiee.getImportante());
        return annonceRepository.save(annonce);
    }

    public void supprimer(Long id) {
        if (!annonceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Annonce introuvable avec l'id " + id);
        }
        annonceRepository.deleteById(id);
    }
}
