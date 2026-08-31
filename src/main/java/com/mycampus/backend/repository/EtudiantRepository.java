package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Etudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EtudiantRepository extends JpaRepository<Etudiant, Long> {
    Optional<Etudiant> findByUtilisateurId(Long utilisateurId);
    Optional<Etudiant> findByMatricule(String matricule);
    boolean existsByMatricule(String matricule);
}
