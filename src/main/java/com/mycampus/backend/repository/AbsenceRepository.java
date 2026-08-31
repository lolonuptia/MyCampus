package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByEtudiantId(Long etudiantId);
}
