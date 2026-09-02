package com.mycampus.android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Etudiant
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.EtudiantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(
        val etudiant: Etudiant,
        val saving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(private val etudiantRepository: EtudiantRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                _uiState.value = ProfileUiState.Loaded(etudiantRepository.getMoi())
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.toUserMessage())
            }
        }
    }

    fun save(filiere: String, niveau: String) {
        val current = (_uiState.value as? ProfileUiState.Loaded)?.etudiant ?: return
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loaded(current, saving = true)
            try {
                val updated = current.copy(
                    filiere = filiere.ifBlank { null },
                    niveau = niveau.ifBlank { null }
                )
                val result = etudiantRepository.modifier(current.id, updated)
                _uiState.value = ProfileUiState.Loaded(result, saved = true)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Loaded(current, error = e.toUserMessage())
            }
        }
    }
}
