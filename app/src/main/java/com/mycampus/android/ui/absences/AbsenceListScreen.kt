package com.mycampus.android.ui.absences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AbsenceListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: AbsenceViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes absences") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is AbsenceUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is AbsenceUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is AbsenceUiState.Success -> {
                    if (state.absences.isEmpty()) {
                        Text("Aucune absence enregistrée", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.absences) { absence ->
                                val justifiee = absence.statut == "JUSTIFIEE"
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        absence.seance?.cours?.let {
                                            Text(it.titre, style = MaterialTheme.typography.titleMedium)
                                        }
                                        absence.seance?.let {
                                            Text("${it.dateSeance} — ${it.heureDebut} à ${it.heureFin}")
                                            it.salle?.let { salle -> Text("Salle : $salle") }
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        AssistChip(
                                            onClick = {},
                                            label = { Text(if (justifiee) "Justifiée" else "Non justifiée") },
                                            colors = AssistChipDefaults.assistChipColors(
                                                containerColor = if (justifiee)
                                                    MaterialTheme.colorScheme.primaryContainer
                                                else MaterialTheme.colorScheme.errorContainer
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
