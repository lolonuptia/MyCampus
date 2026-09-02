package com.mycampus.android.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mycampus.android.ui.ViewModelFactory

@Composable
fun RegisterScreen(
    factory: ViewModelFactory,
    onRegisterSuccess: (role: String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val viewModel: RegisterViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    var nom by remember { mutableStateOf("") }
    var prenom by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var motDePasse by remember { mutableStateOf("") }
    var matricule by remember { mutableStateOf("") }
    var filiere by remember { mutableStateOf("") }
    var niveau by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is LoginUiState.Success) onRegisterSuccess(state.role)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        Text("Créer un compte étudiant", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(24.dp))

        OutlinedTextField(nom, { nom = it }, label = { Text("Nom") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(prenom, { prenom = it }, label = { Text("Prénom") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(email, { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            motDePasse, { motDePasse = it }, label = { Text("Mot de passe (min. 6 caractères)") },
            visualTransformation = PasswordVisualTransformation(), singleLine = true, modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(matricule, { matricule = it }, label = { Text("Matricule") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(filiere, { filiere = it }, label = { Text("Filière (optionnel)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(niveau, { niveau = it }, label = { Text("Niveau (optionnel)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(20.dp))

        if (uiState is LoginUiState.Error) {
            Text(
                (uiState as LoginUiState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }

        Button(
            onClick = { viewModel.register(nom, prenom, email, motDePasse, matricule, filiere, niveau) },
            enabled = uiState !is LoginUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Text("S'inscrire")
            }
        }

        Spacer(Modifier.height(12.dp))
        TextButton(onClick = onNavigateBack) { Text("Retour à la connexion") }
    }
}
