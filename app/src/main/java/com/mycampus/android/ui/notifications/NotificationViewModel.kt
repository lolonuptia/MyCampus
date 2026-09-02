package com.mycampus.android.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Notification
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import com.mycampus.android.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NotificationUiState {
    data object Loading : NotificationUiState
    data class Success(val notifications: List<Notification>) : NotificationUiState
    data class Error(val message: String) : NotificationUiState
}

class NotificationViewModel(
    private val authRepository: AuthRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationUiState>(NotificationUiState.Loading)
    val uiState: StateFlow<NotificationUiState> = _uiState
    private var etudiantId: Long? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NotificationUiState.Loading
            try {
                val id = authRepository.currentEtudiantId()
                    ?: throw IllegalStateException("Aucune fiche étudiant liée à ce compte")
                etudiantId = id
                _uiState.value = NotificationUiState.Success(notificationRepository.getToutes(id))
            } catch (e: Exception) {
                _uiState.value = NotificationUiState.Error(e.toUserMessage())
            }
        }
    }

    fun marquerLue(notificationId: Long) {
        val id = etudiantId ?: return
        viewModelScope.launch {
            try {
                notificationRepository.marquerLue(id, notificationId)
                load() // rafraîchit la liste après marquage
            } catch (_: Exception) {
                // Échec silencieux : la liste reste inchangée, l'utilisateur peut réessayer
            }
        }
    }
}
