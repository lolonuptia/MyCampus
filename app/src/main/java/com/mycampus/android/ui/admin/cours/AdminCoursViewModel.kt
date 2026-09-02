package com.mycampus.android.ui.admin.cours

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Cours
import com.mycampus.android.data.network.ApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AdminUiState {
    data object Loading : AdminUiState()
    data object Success : AdminUiState()
    data class Error(val message: String) : AdminUiState()
}

class AdminCoursViewModel(private val apiService: ApiService) : ViewModel() {

    private val _coursList = MutableStateFlow<List<Cours>>(emptyList())
    val coursList: StateFlow<List<Cours>> = _coursList.asStateFlow()

    private val _uiState = MutableStateFlow<AdminUiState>(AdminUiState.Success)
    val uiState: StateFlow<AdminUiState> = _uiState.asStateFlow()

    init {
        charger()
    }

    fun charger() {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                _coursList.value = apiService.getTousCours()
                _uiState.value = AdminUiState.Success
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Erreur de chargement")
            }
        }
    }

    fun creer(cours: Cours, onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                apiService.creerCours(cours)
                charger()
                onDone()
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Erreur de creation")
            }
        }
    }

    fun modifier(id: Long, cours: Cours, onDone: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                apiService.modifierCours(id, cours)
                charger()
                onDone()
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Erreur de modification")
            }
        }
    }

    fun supprimer(id: Long) {
        viewModelScope.launch {
            _uiState.value = AdminUiState.Loading
            try {
                apiService.supprimerCours(id)
                charger()
            } catch (e: Exception) {
                _uiState.value = AdminUiState.Error(e.message ?: "Erreur de suppression")
            }
        }
    }
}
