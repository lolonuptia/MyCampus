package com.mycampus.backend.service;

import com.mycampus.backend.dto.AuthResponse;
import com.mycampus.backend.dto.LoginRequest;
import com.mycampus.backend.dto.RegisterRequest;
import com.mycampus.backend.entity.Etudiant;
import com.mycampus.backend.entity.Utilisateur;
import com.mycampus.backend.exception.BadRequestException;
import com.mycampus.backend.repository.EtudiantRepository;
import com.mycampus.backend.repository.UtilisateurRepository;
import com.mycampus.backend.security.JwtUtil;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UtilisateurRepository utilisateurRepository;
    private final EtudiantRepository etudiantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UtilisateurRepository utilisateurRepository,
                        EtudiantRepository etudiantRepository,
                        PasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.utilisateurRepository = utilisateurRepository;
        this.etudiantRepository = etudiantRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (utilisateurRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Cet email est déjà utilisé");
        }
        if (etudiantRepository.existsByMatricule(request.getMatricule())) {
            throw new BadRequestException("Ce matricule est déjà utilisé");
        }

        // 1. Créer l'utilisateur (mot de passe hashé, jamais en clair)
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setNom(request.getNom());
        utilisateur.setPrenom(request.getPrenom());
        utilisateur.setEmail(request.getEmail());
        utilisateur.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        utilisateur.setRole("ETUDIANT");
        utilisateur = utilisateurRepository.save(utilisateur);

        // 2. Créer l'étudiant lié
        Etudiant etudiant = new Etudiant();
        etudiant.setUtilisateur(utilisateur);
        etudiant.setMatricule(request.getMatricule());
        etudiant.setFiliere(request.getFiliere());
        etudiant.setNiveau(request.getNiveau());
        etudiant = etudiantRepository.save(etudiant);

        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole(), utilisateur.getId());
        return new AuthResponse(
                token,
                utilisateur.getId(),
                etudiant.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(request.getMotDePasse(), utilisateur.getMotDePasse())) {
            throw new BadCredentialsException("Email ou mot de passe incorrect");
        }

        Long etudiantId = etudiantRepository.findByUtilisateurId(utilisateur.getId())
                .map(Etudiant::getId)
                .orElse(null);

        String token = jwtUtil.generateToken(utilisateur.getEmail(), utilisateur.getRole(), utilisateur.getId());
        return new AuthResponse(
                token,
                utilisateur.getId(),
                etudiantId,
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getRole()
        );
    }
}
