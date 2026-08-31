package com.mycampus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    /** Identifiant de la fiche étudiant (null pour un compte ADMIN). */
    private Long etudiantId;
    private String nom;
    private String prenom;
    private String role;
}
