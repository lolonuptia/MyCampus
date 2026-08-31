package com.mycampus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

// Ne contient JAMAIS le mot de passe - sécurité
@Data
@AllArgsConstructor
public class EtudiantDto {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String matricule;
    private String filiere;
    private String niveau;
}
