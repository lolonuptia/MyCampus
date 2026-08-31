package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Cours;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CoursRepository extends JpaRepository<Cours, Long> {
    List<Cours> findByFiliere(String filiere);
    List<Cours> findByEnseignantId(Long enseignantId);
    List<Cours> findByTitreContainingIgnoreCase(String motCle);
}
