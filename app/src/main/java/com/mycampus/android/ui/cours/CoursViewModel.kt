package com.mycampus.android.ui.cours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Cours
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.CoursRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface CoursUiState {
    data object Loading : CoursUiState
    data class Success(val cours: List<Cours>) : CoursUiState
    data class Error(val message: String) : CoursUiState
}

class CoursViewModel(private val repository: CoursRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<CoursUiState>(CoursUiState.Loading)
    val uiState: StateFlow<CoursUiState> = _uiState

    init {
        loadTous()
    }

    fun loadTous() {
        viewModelScope.launch {
            _uiState.value = CoursUiState.Loading
            try {
                _uiState.value = CoursUiState.Success(repository.getTous())
            } catch (e: Exception) {
                _uiState.value = CoursUiState.Error(e.toUserMessage())
            }
        }
    }

    fun rechercher(motCle: String) {
        if (motCle.isBlank()) { loadTous(); return }
        viewModelScope.launch {
            _uiState.value = CoursUiState.Loading
            try {
                _uiState.value = CoursUiState.Success(repository.rechercher(motCle))
            } catch (e: Exception) {
                _uiState.value = CoursUiState.Error(e.toUserMessage())
            }
        }
    }
}
