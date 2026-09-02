package com.mycampus.android.data.network

import com.mycampus.android.data.dto.*
import retrofit2.http.*

/**
 * Toutes les routes copiées exactement depuis les @RequestMapping / @GetMapping
 * des controllers Java. Vérifie toujours ce fichier contre le backend si tu
 * ajoutes de nouvelles routes côté Spring Boot.
 */
interface ApiService {

    // ---------- AuthController ----------
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    // ---------- EtudiantController ----------
    @GET("api/etudiants")
    suspend fun getTousEtudiants(): List<Etudiant> // admin uniquement

    @GET("api/etudiants/moi")
    suspend fun getMoi(): Etudiant // fiche étudiant liée au JWT courant

    @GET("api/etudiants/{id}")
    suspend fun getEtudiantParId(@Path("id") id: Long): Etudiant

    @PUT("api/etudiants/{id}")
    suspend fun modifierEtudiant(@Path("id") id: Long, @Body etudiant: Etudiant): Etudiant

    @DELETE("api/etudiants/admin/{id}")
    suspend fun supprimerEtudiant(@Path("id") id: Long)

    // ---------- CoursController ----------
    @GET("api/cours")
    suspend fun getTousCours(): List<Cours>

    @GET("api/cours/{id}")
    suspend fun getCoursParId(@Path("id") id: Long): Cours

    @GET("api/cours/recherche")
    suspend fun rechercherCours(@Query("motCle") motCle: String): List<Cours>

    @POST("api/admin/cours")
    suspend fun creerCours(@Body cours: Cours): Cours

    @PUT("api/admin/cours/{id}")
    suspend fun modifierCours(@Path("id") id: Long, @Body cours: Cours): Cours

    @DELETE("api/admin/cours/{id}")
    suspend fun supprimerCours(@Path("id") id: Long)

    // ---------- EnseignantController ----------
    @GET("api/enseignants")
    suspend fun getTousEnseignants(): List<Enseignant>

    @GET("api/enseignants/{id}")
    suspend fun getEnseignantParId(@Path("id") id: Long): Enseignant

    @POST("api/admin/enseignants")
    suspend fun creerEnseignant(@Body enseignant: Enseignant): Enseignant

    @PUT("api/admin/enseignants/{id}")
    suspend fun modifierEnseignant(@Path("id") id: Long, @Body enseignant: Enseignant): Enseignant

    @DELETE("api/admin/enseignants/{id}")
    suspend fun supprimerEnseignant(@Path("id") id: Long)

    // ---------- NoteController ----------
    @GET("api/etudiants/{etudiantId}/notes")
    suspend fun getNotesEtudiant(@Path("etudiantId") etudiantId: Long): List<Note>

    @GET("api/etudiants/{etudiantId}/moyenne")
    suspend fun getMoyenne(@Path("etudiantId") etudiantId: Long): MoyenneResponse

    @POST("api/admin/notes")
    suspend fun ajouterNote(@Body note: Note): Note

    @PUT("api/admin/notes/{id}")
    suspend fun modifierNote(@Path("id") id: Long, @Body note: Note): Note

    @DELETE("api/admin/notes/{id}")
    suspend fun supprimerNote(@Path("id") id: Long)

    // ---------- AbsenceController ----------
    @GET("api/etudiants/{etudiantId}/absences")
    suspend fun getAbsencesEtudiant(@Path("etudiantId") etudiantId: Long): List<Absence>

    @POST("api/admin/absences")
    suspend fun creerAbsence(@Body absence: Absence): Absence

    @PATCH("api/admin/absences/{id}/statut")
    suspend fun modifierStatutAbsence(
        @Path("id") id: Long,
        @Query("statut") statut: String,
        @Query("justificatif") justificatif: String? = null
    ): Absence

    @DELETE("api/admin/absences/{id}")
    suspend fun supprimerAbsence(@Path("id") id: Long)

    // ---------- AnnonceController ----------
    @GET("api/annonces")
    suspend fun getToutesAnnonces(): List<Annonce>

    @GET("api/annonces/recherche")
    suspend fun rechercherAnnonces(@Query("motCle") motCle: String): List<Annonce>

    @POST("api/admin/annonces")
    suspend fun creerAnnonce(@Body annonce: Annonce): Annonce

    @PUT("api/admin/annonces/{id}")
    suspend fun modifierAnnonce(@Path("id") id: Long, @Body annonce: Annonce): Annonce

    @DELETE("api/admin/annonces/{id}")
    suspend fun supprimerAnnonce(@Path("id") id: Long)

    // ---------- NotificationController ----------
    @GET("api/etudiants/{etudiantId}/notifications")
    suspend fun getNotifications(@Path("etudiantId") etudiantId: Long): List<Notification>

    @GET("api/etudiants/{etudiantId}/notifications/non-lues")
    suspend fun getNotificationsNonLues(@Path("etudiantId") etudiantId: Long): List<Notification>

    @PATCH("api/etudiants/{etudiantId}/notifications/{id}/lue")
    suspend fun marquerNotificationLue(
        @Path("etudiantId") etudiantId: Long,
        @Path("id") id: Long
    ): Notification

    // ---------- SeanceController ----------
    @GET("api/seances")
    suspend fun getToutesSeances(): List<Seance>

    @GET("api/cours/{coursId}/seances")
    suspend fun getSeancesParCours(@Path("coursId") coursId: Long): List<Seance>

    @POST("api/admin/seances")
    suspend fun creerSeance(@Body seance: Seance): Seance

    @PUT("api/admin/seances/{id}")
    suspend fun modifierSeance(@Path("id") id: Long, @Body seance: Seance): Seance

    @DELETE("api/admin/seances/{id}")
    suspend fun supprimerSeance(@Path("id") id: Long)
}
