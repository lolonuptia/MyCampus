package com.mycampus.android.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState

    fun register(
        nom: String, prenom: String, email: String, motDePasse: String,
        matricule: String, filiere: String, niveau: String
    ) {
        if (listOf(nom, prenom, email, motDePasse, matricule).any { it.isBlank() }) {
            _uiState.value = LoginUiState.Error("Tous les champs obligatoires doivent être remplis")
            return
        }
        if (motDePasse.length < 6) {
            _uiState.value = LoginUiState.Error("Le mot de passe doit contenir au moins 6 caractères")
            return
        }
        viewModelScope.launch {
            _uiState.value = LoginUiState.Loading
            try {
                val response = authRepository.register(
                    nom.trim(), prenom.trim(), email.trim(), motDePasse,
                    matricule.trim(), filiere.ifBlank { null }, niveau.ifBlank { null }
                )
                _uiState.value = LoginUiState.Success(response.role)
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error(e.toUserMessage())
            }
        }
    }
}
