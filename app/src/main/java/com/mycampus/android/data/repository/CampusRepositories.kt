package com.mycampus.android.data.repository

import com.mycampus.android.data.dto.*
import com.mycampus.android.data.network.ApiService

class EtudiantRepository(private val api: ApiService) {
    suspend fun getMoi(): Etudiant = api.getMoi()
    suspend fun getParId(id: Long): Etudiant = api.getEtudiantParId(id)
    suspend fun getTous(): List<Etudiant> = api.getTousEtudiants() // admin
    suspend fun modifier(id: Long, etudiant: Etudiant): Etudiant = api.modifierEtudiant(id, etudiant)
}

class CoursRepository(private val api: ApiService) {
    suspend fun getTous(): List<Cours> = api.getTousCours()
    suspend fun getParId(id: Long): Cours = api.getCoursParId(id)
    suspend fun rechercher(motCle: String): List<Cours> = api.rechercherCours(motCle)
    suspend fun creer(cours: Cours): Cours = api.creerCours(cours) // admin
    suspend fun modifier(id: Long, cours: Cours): Cours = api.modifierCours(id, cours) // admin
    suspend fun supprimer(id: Long) = api.supprimerCours(id) // admin
}

class EnseignantRepository(private val api: ApiService) {
    suspend fun getTous(): List<Enseignant> = api.getTousEnseignants()
    suspend fun getParId(id: Long): Enseignant = api.getEnseignantParId(id)
}

class NoteRepository(private val api: ApiService) {
    suspend fun getNotes(etudiantId: Long): List<Note> = api.getNotesEtudiant(etudiantId)
    suspend fun getMoyenne(etudiantId: Long): MoyenneResponse = api.getMoyenne(etudiantId)
    suspend fun ajouter(note: Note): Note = api.ajouterNote(note) // admin
    suspend fun modifier(id: Long, note: Note): Note = api.modifierNote(id, note) // admin
    suspend fun supprimer(id: Long) = api.supprimerNote(id) // admin
}

class AbsenceRepository(private val api: ApiService) {
    suspend fun getAbsences(etudiantId: Long): List<Absence> = api.getAbsencesEtudiant(etudiantId)
    suspend fun creer(absence: Absence): Absence = api.creerAbsence(absence) // admin
    suspend fun modifierStatut(id: Long, statut: String, justificatif: String?): Absence =
        api.modifierStatutAbsence(id, statut, justificatif) // admin
    suspend fun supprimer(id: Long) = api.supprimerAbsence(id) // admin
}

class AnnonceRepository(private val api: ApiService) {
    suspend fun getToutes(): List<Annonce> = api.getToutesAnnonces()
    suspend fun rechercher(motCle: String): List<Annonce> = api.rechercherAnnonces(motCle)
    suspend fun creer(annonce: Annonce): Annonce = api.creerAnnonce(annonce) // admin
    suspend fun modifier(id: Long, annonce: Annonce): Annonce = api.modifierAnnonce(id, annonce) // admin
    suspend fun supprimer(id: Long) = api.supprimerAnnonce(id) // admin
}

class NotificationRepository(private val api: ApiService) {
    suspend fun getToutes(etudiantId: Long): List<Notification> = api.getNotifications(etudiantId)
    suspend fun getNonLues(etudiantId: Long): List<Notification> = api.getNotificationsNonLues(etudiantId)
    suspend fun marquerLue(etudiantId: Long, id: Long): Notification =
        api.marquerNotificationLue(etudiantId, id)
}

class SeanceRepository(private val api: ApiService) {
    suspend fun getToutes(): List<Seance> = api.getToutesSeances()
    suspend fun getParCours(coursId: Long): List<Seance> = api.getSeancesParCours(coursId)
    suspend fun creer(seance: Seance): Seance = api.creerSeance(seance) // admin
    suspend fun modifier(id: Long, seance: Seance): Seance = api.modifierSeance(id, seance) // admin
    suspend fun supprimer(id: Long) = api.supprimerSeance(id) // admin
}
