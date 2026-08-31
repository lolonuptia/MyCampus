package com.mycampus.backend.repository;

import com.mycampus.backend.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
    List<Annonce> findAllByOrderByDatePublicationDesc();
    List<Annonce> findByTitreContainingIgnoreCase(String motCle);
}
