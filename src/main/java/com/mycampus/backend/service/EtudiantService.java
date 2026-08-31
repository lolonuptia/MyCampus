package com.mycampus.backend.service;

import com.mycampus.backend.entity.Etudiant;
import com.mycampus.backend.entity.Utilisateur;
import com.mycampus.backend.exception.ResourceNotFoundException;
import com.mycampus.backend.repository.EtudiantRepository;
import com.mycampus.backend.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EtudiantService {

    private final EtudiantRepository etudiantRepository;
    private final UtilisateurRepository utilisateurRepository;

    public EtudiantService(EtudiantRepository etudiantRepository,
                           UtilisateurRepository utilisateurRepository) {
        this.etudiantRepository = etudiantRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    public Etudiant getParEmail(String email) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
        return etudiantRepository.findByUtilisateurId(utilisateur.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Aucune fiche étudiant pour ce compte"));
    }

    public List<Etudiant> getTous() {
        return etudiantRepository.findAll();
    }

    public Etudiant getParId(Long id) {
        return etudiantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Étudiant introuvable avec l'id " + id));
    }

    public Etudiant modifier(Long id, Etudiant modifie) {
        Etudiant etudiant = getParId(id);
        etudiant.setFiliere(modifie.getFiliere());
        etudiant.setNiveau(modifie.getNiveau());
        return etudiantRepository.save(etudiant);
    }

    public void supprimer(Long id) {
        if (!etudiantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Étudiant introuvable avec l'id " + id);
        }
        etudiantRepository.deleteById(id);
    }
}
