package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByEtudiantId(Long etudiantId);
    List<Note> findByEtudiantIdAndCoursId(Long etudiantId, Long coursId);
}
