package com.mycampus.android.ui.annonces

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnonceListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: AnnonceViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Annonces") },
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
                is AnnonceUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is AnnonceUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is AnnonceUiState.Success -> {
                    if (state.annonces.isEmpty()) {
                        Text("Aucune annonce", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.annonces) { annonce ->
                                ElevatedCard(modifier = androidx.compose.ui.Modifier.fillMaxWidth()) {
                                    Column(Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            if (annonce.importante == true) {
                                                Icon(
                                                    Icons.Default.Warning,
                                                    contentDescription = "Importante",
                                                    tint = MaterialTheme.colorScheme.error,
                                                    modifier = Modifier.padding(end = 4.dp)
                                                )
                                            }
                                            Text(annonce.titre, style = MaterialTheme.typography.titleMedium)
                                        }
                                        Spacer(Modifier.height(4.dp))
                                        Text(annonce.contenu, style = MaterialTheme.typography.bodyMedium)
                                        annonce.datePublication?.let {
                                            Spacer(Modifier.height(4.dp))
                                            Text(it, style = MaterialTheme.typography.bodySmall)
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
