package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Seance;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SeanceRepository extends JpaRepository<Seance, Long> {
    List<Seance> findByCoursId(Long coursId);
}
