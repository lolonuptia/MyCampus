package com.mycampus.android.ui.admin.cours

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mycampus.android.data.dto.Cours

@Composable
fun AdminCoursFormDialog(
    coursExistant: Cours?,
    onDismiss: () -> Unit,
    onSubmit: (titre: String, description: String, filiere: String) -> Unit
) {
    var titre by remember { mutableStateOf(coursExistant?.titre ?: "") }
    var description by remember { mutableStateOf(coursExistant?.description ?: "") }
    var filiere by remember { mutableStateOf(coursExistant?.filiere ?: "") }
    var erreur by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (coursExistant == null) "Nouveau cours" else "Modifier le cours") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = titre,
                    onValueChange = { titre = it },
                    label = { Text("Titre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                OutlinedTextField(
                    value = filiere,
                    onValueChange = { filiere = it },
                    label = { Text("Filiere") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                erreur?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (titre.isBlank()) {
                    erreur = "Le titre est obligatoire"
                } else {
                    onSubmit(titre, description, filiere)
                }
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Annuler")
            }
        }
    )
}
