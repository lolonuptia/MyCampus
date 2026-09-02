package com.mycampus.android.ui.cours

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
fun CoursListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: CoursViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    var recherche by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cours") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = recherche,
                onValueChange = {
                    recherche = it
                    viewModel.rechercher(it)
                },
                label = { Text("Rechercher un cours") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(12.dp))

            when (val state = uiState) {
                is CoursUiState.Loading -> Box(Modifier.fillMaxSize()) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
                is CoursUiState.Error -> Text(state.message, color = MaterialTheme.colorScheme.error)
                is CoursUiState.Success -> {
                    if (state.cours.isEmpty()) {
                        Text("Aucun cours trouvé")
                    } else {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(state.cours) { cours ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Text(cours.titre, style = MaterialTheme.typography.titleMedium)
                                        cours.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                                        cours.enseignant?.let {
                                            Text("Enseignant : ${it.prenom} ${it.nom}", style = MaterialTheme.typography.bodySmall)
                                        }
                                        cours.filiere?.let {
                                            Text("Filière : $it", style = MaterialTheme.typography.bodySmall)
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
}
