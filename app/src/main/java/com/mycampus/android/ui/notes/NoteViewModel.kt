package com.mycampus.android.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Note
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import com.mycampus.android.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NoteUiState {
    data object Loading : NoteUiState
    data class Success(val notes: List<Note>) : NoteUiState
    data class Error(val message: String) : NoteUiState
}

class NoteViewModel(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Loading)
    val uiState: StateFlow<NoteUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NoteUiState.Loading
            try {
                val etudiantId = authRepository.currentEtudiantId()
                    ?: throw IllegalStateException("Aucune fiche Ã©tudiant liÃ©e Ã  ce compte")
                _uiState.value = NoteUiState.Success(noteRepository.getNotes(etudiantId))
            } catch (e: Exception) {
                _uiState.value = NoteUiState.Error(e.toUserMessage())
            }
        }
    }
}
