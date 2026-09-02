package com.mycampus.android.ui.seances

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
fun SeanceListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: SeanceViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emploi du temps") },
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
                is SeanceUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is SeanceUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is SeanceUiState.Success -> {
                    if (state.seances.isEmpty()) {
                        Text("Aucune sÃ©ance programmÃ©e", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.seances) { seance ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(
                                            seance.cours?.titre ?: "Cours",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        Text("${seance.dateSeance} de ${seance.heureDebut} Ã  ${seance.heureFin}")
                                        seance.salle?.let { Text("Salle : $it") }
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
