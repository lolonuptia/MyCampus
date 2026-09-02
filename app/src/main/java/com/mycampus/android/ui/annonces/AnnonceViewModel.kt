package com.mycampus.android.ui.annonces

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Annonce
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AnnonceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface AnnonceUiState {
    data object Loading : AnnonceUiState
    data class Success(val annonces: List<Annonce>) : AnnonceUiState
    data class Error(val message: String) : AnnonceUiState
}

class AnnonceViewModel(private val repository: AnnonceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<AnnonceUiState>(AnnonceUiState.Loading)
    val uiState: StateFlow<AnnonceUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = AnnonceUiState.Loading
            try {
                _uiState.value = AnnonceUiState.Success(repository.getToutes())
            } catch (e: Exception) {
                _uiState.value = AnnonceUiState.Error(e.toUserMessage())
            }
        }
    }
}
