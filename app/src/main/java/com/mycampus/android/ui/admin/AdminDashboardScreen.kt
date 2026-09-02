package com.mycampus.android.ui.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class AdminMenuItem(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    onNavigateToCours: () -> Unit,
    onNavigateToEnseignants: () -> Unit,
    onNavigateToEtudiants: () -> Unit,
    onNavigateToNotes: () -> Unit,
    onNavigateToAbsences: () -> Unit,
    onNavigateToAnnonces: () -> Unit,
    onNavigateToSeances: () -> Unit,
    onLogout: () -> Unit
) {
    val items = listOf(
        AdminMenuItem("Cours", Icons.AutoMirrored.Filled.MenuBook, onNavigateToCours),
        AdminMenuItem("Enseignants", Icons.Default.Person, onNavigateToEnseignants),
        AdminMenuItem("Etudiants", Icons.Default.School, onNavigateToEtudiants),
        AdminMenuItem("Notes", Icons.Default.Grade, onNavigateToNotes),
        AdminMenuItem("Absences", Icons.Default.EventBusy, onNavigateToAbsences),
        AdminMenuItem("Annonces", Icons.AutoMirrored.Filled.Announcement, onNavigateToAnnonces),
        AdminMenuItem("Seances", Icons.Default.Schedule, onNavigateToSeances),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Administration") },
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Deconnexion")
                    }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            items(items) { item ->
                Card(
                    onClick = item.onClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.3f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(item.label, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }
        }
    }
}
