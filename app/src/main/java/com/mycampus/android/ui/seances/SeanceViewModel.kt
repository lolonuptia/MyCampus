package com.mycampus.android.ui.seances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Seance
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.CoursRepository
import com.mycampus.android.data.repository.EtudiantRepository
import com.mycampus.android.data.repository.SeanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SeanceUiState {
    data object Loading : SeanceUiState
    data class Success(val seances: List<Seance>) : SeanceUiState
    data class Error(val message: String) : SeanceUiState
}

/**
 * Le backend ne modÃ©lise pas d'inscription explicite Ã©tudiant <-> cours.
 * On rapproche donc les sÃ©ances pertinentes en filtrant les cours dont la
 * filiÃ¨re correspond Ã  celle de l'Ã©tudiant connectÃ©. Si un jour une vraie
 * table d'inscription existe cÃ´tÃ© API, remplace ce filtre par l'appel dÃ©diÃ©.
 */
class SeanceViewModel(
    private val etudiantRepository: EtudiantRepository,
    private val coursRepository: CoursRepository,
    private val seanceRepository: SeanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeanceUiState>(SeanceUiState.Loading)
    val uiState: StateFlow<SeanceUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SeanceUiState.Loading
            try {
                val etudiant = etudiantRepository.getMoi()
                val tousLesCours = coursRepository.getTous()
                val coursConcernes = if (etudiant.filiere.isNullOrBlank()) {
                    tousLesCours
                } else {
                    tousLesCours.filter { it.filiere == etudiant.filiere }
                }

                val seances = coursConcernes
                    .flatMap { cours -> seanceRepository.getParCours(cours.id) }
                    .sortedWith(compareBy({ it.dateSeance }, { it.heureDebut }))

                _uiState.value = SeanceUiState.Success(seances)
            } catch (e: Exception) {
                _uiState.value = SeanceUiState.Error(e.toUserMessage())
            }
        }
    }
}
