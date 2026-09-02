package com.mycampus.android.ui.absences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Absence
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AbsenceRepository
import com.mycampus.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AbsenceUiState {
    data object Loading : AbsenceUiState
    data class Success(val absences: List<Absence>) : AbsenceUiState
    data class Error(val message: String) : AbsenceUiState
}

class AbsenceViewModel(
    private val authRepository: AuthRepository,
    private val absenceRepository: AbsenceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AbsenceUiState>(AbsenceUiState.Loading)
    val uiState: StateFlow<AbsenceUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = AbsenceUiState.Loading
            try {
                val etudiantId = authRepository.currentEtudiantId()
                    ?: throw IllegalStateException("Aucune fiche étudiant liée à ce compte")
                _uiState.value = AbsenceUiState.Success(absenceRepository.getAbsences(etudiantId))
            } catch (e: Exception) {
                _uiState.value = AbsenceUiState.Error(e.toUserMessage())
            }
        }
    }
}
