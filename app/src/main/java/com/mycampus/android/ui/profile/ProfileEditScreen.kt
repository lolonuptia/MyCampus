package com.mycampus.android.ui.profile

import androidx.compose.foundation.layout.*
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
fun ProfileEditScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: ProfileViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon profil") },
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
                is ProfileUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is ProfileUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is ProfileUiState.Loaded -> {
                    var filiere by remember(state.etudiant.id) { mutableStateOf(state.etudiant.filiere ?: "") }
                    var niveau by remember(state.etudiant.id) { mutableStateOf(state.etudiant.niveau ?: "") }

                    Column(modifier = Modifier.padding(24.dp).fillMaxSize()) {
                        val u = state.etudiant.utilisateur
                        Text("${u?.prenom ?: ""} ${u?.nom ?: ""}", style = MaterialTheme.typography.titleLarge)
                        Text(u?.email ?: "", style = MaterialTheme.typography.bodyMedium)
                        Text("Matricule : ${state.etudiant.matricule}", style = MaterialTheme.typography.bodyMedium)

                        Spacer(Modifier.height(24.dp))

                        OutlinedTextField(
                            value = filiere,
                            onValueChange = { filiere = it },
                            label = { Text("FiliÃ¨re") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(
                            value = niveau,
                            onValueChange = { niveau = it },
                            label = { Text("Niveau") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(Modifier.height(20.dp))

                        state.error?.let {
                            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 12.dp))
                        }
                        if (state.saved) {
                            Text(
                                "Profil mis Ã  jour âœ“",
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 12.dp)
                            )
                        }

                        Button(
                            onClick = { viewModel.save(filiere, niveau) },
                            enabled = !state.saving,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            if (state.saving) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                            } else {
                                Text("Enregistrer")
                            }
                        }
                    }
                }
            }
        }
    }
}
