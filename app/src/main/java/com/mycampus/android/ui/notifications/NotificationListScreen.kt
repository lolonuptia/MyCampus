package com.mycampus.android.ui.notifications

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.ui.ViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: NotificationViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
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
                is NotificationUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is NotificationUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is NotificationUiState.Success -> {
                    if (state.notifications.isEmpty()) {
                        Text("Aucune notification", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.notifications) { notif ->
                                val lue = notif.lu == true
                                ElevatedCard(
                                    onClick = { if (!lue) viewModel.marquerLue(notif.id) },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        if (!lue) {
                                            Icon(
                                                Icons.Default.Circle,
                                                contentDescription = "Non lue",
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(10.dp).padding(top = 6.dp, end = 8.dp)
                                            )
                                        } else {
                                            Spacer(Modifier.width(18.dp))
                                        }
                                        Column {
                                            Text(
                                                notif.annonce?.titre ?: "Notification",
                                                style = if (lue) MaterialTheme.typography.bodyLarge
                                                        else MaterialTheme.typography.titleMedium
                                            )
                                            notif.annonce?.contenu?.let {
                                                Text(it, style = MaterialTheme.typography.bodyMedium)
                                            }
                                            notif.dateNotif?.let {
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
}
