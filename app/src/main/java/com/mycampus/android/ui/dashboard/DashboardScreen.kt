package com.mycampus.android.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    factory: ViewModelFactory,
    onLogout: () -> Unit,
    onNavigateToCours: () -> Unit,
    onNavigateToAnnonces: () -> Unit,
    onNavigateToAbsences: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToSeances: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val viewModel: DashboardViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mon espace") },
                actions = {
                    IconButton(onClick = { viewModel.logout(onLogout) }) {
                        Icon(Icons.Default.Logout
, contentDescription = "DÃ©connexion")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (val state = uiState) {
                is DashboardUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is DashboardUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.height(12.dp))
                        Button(onClick = { viewModel.load() }) { Text("RÃ©essayer") }
                    }
                }

                is DashboardUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(16.dp)) {
                                val u = state.etudiant.utilisateur
                                Text(
                                    "${u?.prenom ?: ""} ${u?.nom ?: ""}",
                                    style = MaterialTheme.typography.titleLarge
                                )
                                Text("Matricule : ${state.etudiant.matricule}")
                                state.etudiant.filiere?.let { Text("FiliÃ¨re : $it") }
                                state.etudiant.niveau?.let { Text("Niveau : $it") }

                                Spacer(Modifier.height(12.dp))
                                Divider()
                                Spacer(Modifier.height(12.dp))

                                val moyenne = state.moyenne
                                when (moyenne.statut) {
                                    "AUCUNE_NOTE" -> Text("Aucune note enregistrÃ©e pour le moment")
                                    else -> {
                                        Text(
                                            "Moyenne gÃ©nÃ©rale : ${moyenne.moyenne ?: "-"}/20",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        val couleur = if (moyenne.statut == "ADMIS")
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        Text(
                                            text = if (moyenne.statut == "ADMIS") "Admis(e)" else "AjournÃ©(e)",
                                            color = couleur,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text("${moyenne.nombreNotes} note(s) prise(s) en compte")
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        Text("AccÃ¨s rapide", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        val items = listOf(
                            Triple("Cours", Icons.Default.MenuBook, onNavigateToCours),
                            Triple("Notes", Icons.Default.Grade, onNavigateToNotes),
                            Triple("Emploi du temps", Icons.Default.Schedule, onNavigateToSeances),
                            Triple("Absences", Icons.Default.EventBusy, onNavigateToAbsences),
                            Triple("Annonces", Icons.Default.Announcement
, onNavigateToAnnonces),
                            Triple("Notifications", Icons.Default.Notifications, onNavigateToNotifications),
                            Triple("Mon profil", Icons.Default.Person, onNavigateToProfile)
                        )

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(items) { (label, icon, onClick) ->
                                DashboardTile(label, icon, onClick)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DashboardTile(label: String, icon: ImageVector, onClick: () -> Unit) {
    ElevatedCard(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(100.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = label)
            Spacer(Modifier.height(8.dp))
            Text(label, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        }
    }
}
