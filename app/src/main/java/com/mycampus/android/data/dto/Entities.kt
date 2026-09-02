package com.mycampus.android.data.dto

// Toutes les dates (LocalDate/LocalTime/LocalDateTime) arrivent en String ISO-8601
// car spring.jackson.serialization.write-dates-as-timestamps=false est activé côté backend.
// ex: LocalDate -> "2026-08-31", LocalTime -> "14:30:00", LocalDateTime -> "2026-08-31T14:30:00"

data class Utilisateur(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val role: String, // "ETUDIANT" ou "ADMIN"  (motDePasse n'est jamais envoyé, @JsonIgnore côté backend)
    val dateCreation: String? = null
)

data class Etudiant(
    val id: Long,
    val utilisateur: Utilisateur? = null,
    val matricule: String,
    val filiere: String? = null,
    val niveau: String? = null
)

data class Enseignant(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String? = null,
    val specialite: String? = null
)

data class Cours(
    val id: Long,
    val titre: String,
    val description: String? = null,
    val enseignant: Enseignant? = null,
    val filiere: String? = null
)

data class Seance(
    val id: Long,
    val cours: Cours? = null,
    val dateSeance: String,   // "yyyy-MM-dd"
    val heureDebut: String,   // "HH:mm:ss"
    val heureFin: String,     // "HH:mm:ss"
    val salle: String? = null
)

data class Note(
    val id: Long,
    val etudiant: Etudiant? = null,
    val cours: Cours? = null,
    val valeur: Double,
    val typeEvaluation: String? = null,
    val dateNote: String? = null
)

data class Absence(
    val id: Long,
    val etudiant: Etudiant? = null,
    val seance: Seance? = null,
    val statut: String, // "JUSTIFIEE" ou "NON_JUSTIFIEE"
    val justificatif: String? = null
)

data class Annonce(
    val id: Long,
    val titre: String,
    val contenu: String,
    val datePublication: String? = null,
    val importante: Boolean? = false
)

data class Notification(
    val id: Long,
    val etudiant: Etudiant? = null,
    val annonce: Annonce? = null,
    val lu: Boolean? = false,
    val dateNotif: String? = null
)
