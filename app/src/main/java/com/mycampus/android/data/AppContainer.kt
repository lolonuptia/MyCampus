package com.mycampus.android.data

import android.content.Context
import com.mycampus.android.data.network.RetrofitClient
import com.mycampus.android.data.repository.*

/**
 * Petit conteneur manuel (pas de Hilt pour rester simple).
 * Instancié une seule fois dans MyCampusApplication et accessible partout.
 */
class AppContainer(context: Context) {
    val tokenManager = TokenManager(context.applicationContext)
    private val api = RetrofitClient.getInstance(tokenManager)

    val authRepository = AuthRepository(api, tokenManager)
    val etudiantRepository = EtudiantRepository(api)
    val coursRepository = CoursRepository(api)
    val enseignantRepository = EnseignantRepository(api)
    val noteRepository = NoteRepository(api)
    val absenceRepository = AbsenceRepository(api)
    val annonceRepository = AnnonceRepository(api)
    val notificationRepository = NotificationRepository(api)
    val seanceRepository = SeanceRepository(api)
}
