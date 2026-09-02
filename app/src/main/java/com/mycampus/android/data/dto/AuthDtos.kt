package com.mycampus.android.data.dto

// Correspond exactement à LoginRequest.java
data class LoginRequest(
    val email: String,
    val motDePasse: String
)

// Correspond exactement à RegisterRequest.java
data class RegisterRequest(
    val nom: String,
    val prenom: String,
    val email: String,
    val motDePasse: String,
    val matricule: String,
    val filiere: String? = null,
    val niveau: String? = null
)

// Correspond exactement à AuthResponse.java
data class AuthResponse(
    val token: String,
    val userId: Long,
    val etudiantId: Long?,
    val nom: String,
    val prenom: String,
    val role: String // "ETUDIANT" ou "ADMIN"
)

// Correspond à la Map<String,Object> renvoyée par NoteController#getMoyenne
data class MoyenneResponse(
    val moyenne: Double?,
    val statut: String,     // "ADMIS" | "AJOURNE" | "AUCUNE_NOTE"
    val nombreNotes: Int
)

// Corps utilisé pour PATCH /admin/absences/{id}/statut (envoyé en query params, voir AbsenceRepository ci-dessous)
