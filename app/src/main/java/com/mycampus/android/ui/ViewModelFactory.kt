package com.mycampus.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mycampus.android.data.AppContainer
import com.mycampus.android.ui.absences.AbsenceViewModel
import com.mycampus.android.ui.annonces.AnnonceViewModel
import com.mycampus.android.ui.auth.LoginViewModel
import com.mycampus.android.ui.auth.RegisterViewModel
import com.mycampus.android.ui.cours.CoursViewModel
import com.mycampus.android.ui.dashboard.DashboardViewModel
import com.mycampus.android.ui.notes.NoteViewModel
import com.mycampus.android.ui.notifications.NotificationViewModel
import com.mycampus.android.ui.profile.ProfileViewModel
import com.mycampus.android.ui.seances.SeanceViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(
                    container.authRepository,
                    container.etudiantRepository,
                    container.noteRepository
                ) as T

            modelClass.isAssignableFrom(CoursViewModel::class.java) ->
                CoursViewModel(container.coursRepository) as T

            modelClass.isAssignableFrom(AnnonceViewModel::class.java) ->
                AnnonceViewModel(container.annonceRepository) as T

            modelClass.isAssignableFrom(AbsenceViewModel::class.java) ->
                AbsenceViewModel(container.authRepository, container.absenceRepository) as T

            modelClass.isAssignableFrom(NotificationViewModel::class.java) ->
                NotificationViewModel(container.authRepository, container.notificationRepository) as T

            modelClass.isAssignableFrom(NoteViewModel::class.java) ->
                NoteViewModel(container.authRepository, container.noteRepository) as T

            modelClass.isAssignableFrom(SeanceViewModel::class.java) ->
                SeanceViewModel(
                    container.etudiantRepository,
                    container.coursRepository,
                    container.seanceRepository
                ) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(container.etudiantRepository) as T

            else -> throw IllegalArgumentException("ViewModel inconnu : ${modelClass.name}")
        }
    }
}
