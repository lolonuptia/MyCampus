package com.mycampus.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val role: String) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun login(email: String, motDePasse: String) {
        if (email.isBlank() || motDePasse.isBlank()) {
            _uiState.value = LoginUiState.Error("Renseigne l'email et le mot de passe")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = authRepository.login(email.trim(), motDePasse)
                _uiState.value = LoginUiState.Success(response.role)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.toUserMessage())
            }
        }
    }

    fun resetState() {
        _uiState.value = LoginUiState.Idle
    }
}
