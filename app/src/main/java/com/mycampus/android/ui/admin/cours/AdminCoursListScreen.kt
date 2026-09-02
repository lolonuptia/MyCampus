package com.mycampus.android.ui.admin.cours

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.data.dto.Cours
import com.mycampus.android.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCoursListScreen(
    factory: ViewModelFactory,
    onBack: () -> Unit
) {
    val viewModel: AdminCoursViewModel = viewModel(factory = factory)
    val coursList by viewModel.coursList.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    var showForm by remember { mutableStateOf(false) }
    var coursEnEdition by remember { mutableStateOf<Cours?>(null) }
    var coursASupprimer by remember { mutableStateOf<Cours?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des cours") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coursEnEdition = null
                showForm = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un cours")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (uiState is AdminUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            if (uiState is AdminUiState.Error) {
                Text(
                    text = (uiState as AdminUiState.Error).message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(coursList) { cours ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cours.titre, style = MaterialTheme.typography.titleMedium)
                                cours.filiere?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            IconButton(onClick = {
                                coursEnEdition = cours
                                showForm = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Modifier")
                            }
                            IconButton(onClick = { coursASupprimer = cours }) {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Supprimer",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        AdminCoursFormDialog(
            coursExistant = coursEnEdition,
            onDismiss = { showForm = false },
            onSubmit = { titre, description, filiere ->
                val cours = Cours(
                    id = coursEnEdition?.id ?: 0,
                    titre = titre,
                    description = description,
                    enseignant = coursEnEdition?.enseignant,
                    filiere = filiere
                )
                if (coursEnEdition == null) {
                    viewModel.creer(cours) { showForm = false }
                } else {
                    viewModel.modifier(cours.id, cours) { showForm = false }
                }
            }
        )
    }

    coursASupprimer?.let { cours ->
        AlertDialog(
            onDismissRequest = { coursASupprimer = null },
            title = { Text("Supprimer ce cours ?") },
            text = { Text("\"${cours.titre}\" sera definitivement supprime.") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.supprimer(cours.id)
                    coursASupprimer = null
                }) {
                    Text("Supprimer", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { coursASupprimer = null }) {
                    Text("Annuler")
                }
            }
        )
    }
}
