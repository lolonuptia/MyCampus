package com.mycampus.android.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Etudiant
import com.mycampus.android.data.dto.MoyenneResponse
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import com.mycampus.android.data.repository.EtudiantRepository
import com.mycampus.android.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface DashboardUiState {
    data object Loading : DashboardUiState
    data class Success(val etudiant: Etudiant, val moyenne: MoyenneResponse) : DashboardUiState
    data class Error(val message: String) : DashboardUiState
}

class DashboardViewModel(
    private val authRepository: AuthRepository,
    private val etudiantRepository: EtudiantRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<DashboardUiState>(DashboardUiState.Loading)
    val uiState: StateFlow<DashboardUiState> = _uiState

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState.Loading
            try {
                // La fiche étudiant du compte connecté (déduite du JWT côté backend)
                val etudiant = etudiantRepository.getMoi()
                val moyenne = noteRepository.getMoyenne(etudiant.id)
                _uiState.value = DashboardUiState.Success(etudiant, moyenne)
            } catch (e: Exception) {
                _uiState.value = DashboardUiState.Error(e.toUserMessage())
            }
        }
    }

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            onDone()
        }
    }
}
