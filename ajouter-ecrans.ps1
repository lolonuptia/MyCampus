$base = "C:\MyCampus\frontend\app\src\main\java\com\mycampus\android"

New-Item -ItemType Directory -Force -Path "$base\ui\notes","$base\ui\seances","$base\ui\profile" | Out-Null

# ============================================================
# 1. NOTES
# ============================================================

Set-Content -Path "$base\ui\notes\NoteViewModel.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Note
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.AuthRepository
import com.mycampus.android.data.repository.NoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface NoteUiState {
    data object Loading : NoteUiState
    data class Success(val notes: List<Note>) : NoteUiState
    data class Error(val message: String) : NoteUiState
}

class NoteViewModel(
    private val authRepository: AuthRepository,
    private val noteRepository: NoteRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<NoteUiState>(NoteUiState.Loading)
    val uiState: StateFlow<NoteUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = NoteUiState.Loading
            try {
                val etudiantId = authRepository.currentEtudiantId()
                    ?: throw IllegalStateException("Aucune fiche étudiant liée à ce compte")
                _uiState.value = NoteUiState.Success(noteRepository.getNotes(etudiantId))
            } catch (e: Exception) {
                _uiState.value = NoteUiState.Error(e.toUserMessage())
            }
        }
    }
}
'@

Set-Content -Path "$base\ui\notes\NoteListScreen.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.notes

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
fun NoteListScreen(factory: ViewModelFactory, onBack: () -> Unit) {
    val viewModel: NoteViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mes notes") },
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
                is NoteUiState.Loading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                is NoteUiState.Error -> Text(
                    state.message,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center).padding(24.dp)
                )
                is NoteUiState.Success -> {
                    if (state.notes.isEmpty()) {
                        Text("Aucune note pour le moment", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(state.notes) { note ->
                                ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                note.cours?.titre ?: "Cours",
                                                style = MaterialTheme.typography.titleMedium
                                            )
                                            note.typeEvaluation?.let {
                                                Text(it, style = MaterialTheme.typography.bodySmall)
                                            }
                                            note.dateNote?.let {
                                                Text(it, style = MaterialTheme.typography.bodySmall)
                                            }
                                        }
                                        Text(
                                            "${note.valeur}/20",
                                            style = MaterialTheme.typography.titleLarge,
                                            color = if (note.valeur >= 10)
                                                MaterialTheme.colorScheme.primary
                                            else MaterialTheme.colorScheme.error
                                        )
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
'@

# ============================================================
# 2. EMPLOI DU TEMPS (séances)
# ============================================================

Set-Content -Path "$base\ui\seances\SeanceViewModel.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.seances

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Seance
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.CoursRepository
import com.mycampus.android.data.repository.EtudiantRepository
import com.mycampus.android.data.repository.SeanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface SeanceUiState {
    data object Loading : SeanceUiState
    data class Success(val seances: List<Seance>) : SeanceUiState
    data class Error(val message: String) : SeanceUiState
}

/**
 * Le backend ne modélise pas d'inscription explicite étudiant <-> cours.
 * On rapproche donc les séances pertinentes en filtrant les cours dont la
 * filière correspond à celle de l'étudiant connecté. Si un jour une vraie
 * table d'inscription existe côté API, remplace ce filtre par l'appel dédié.
 */
class SeanceViewModel(
    private val etudiantRepository: EtudiantRepository,
    private val coursRepository: CoursRepository,
    private val seanceRepository: SeanceRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeanceUiState>(SeanceUiState.Loading)
    val uiState: StateFlow<SeanceUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = SeanceUiState.Loading
            try {
                val etudiant = etudiantRepository.getMoi()
                val tousLesCours = coursRepository.getTous()
                val coursConcernes = if (etudiant.filiere.isNullOrBlank()) {
                    tousLesCours
                } else {
                    tousLesCours.filter { it.filiere == etudiant.filiere }
                }

                val seances = coursConcernes
                    .flatMap { cours -> seanceRepository.getParCours(cours.id) }
                    .sortedWith(compareBy({ it.dateSeance }, { it.heureDebut }))

                _uiState.value = SeanceUiState.Success(seances)
            } catch (e: Exception) {
                _uiState.value = SeanceUiState.Error(e.toUserMessage())
            }
        }
    }
}
'@

Set-Content -Path "$base\ui\seances\SeanceListScreen.kt" -Encoding utf8 -Value @'
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
                        Text("Aucune séance programmée", modifier = Modifier.align(Alignment.Center))
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
                                        Text("${seance.dateSeance} de ${seance.heureDebut} à ${seance.heureFin}")
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
'@

# ============================================================
# 3. PROFIL (modifier filière/niveau)
# ============================================================

Set-Content -Path "$base\ui\profile\ProfileViewModel.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mycampus.android.data.dto.Etudiant
import com.mycampus.android.data.network.toUserMessage
import com.mycampus.android.data.repository.EtudiantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed interface ProfileUiState {
    data object Loading : ProfileUiState
    data class Loaded(
        val etudiant: Etudiant,
        val saving: Boolean = false,
        val error: String? = null,
        val saved: Boolean = false
    ) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(private val etudiantRepository: EtudiantRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loading
            try {
                _uiState.value = ProfileUiState.Loaded(etudiantRepository.getMoi())
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error(e.toUserMessage())
            }
        }
    }

    fun save(filiere: String, niveau: String) {
        val current = (_uiState.value as? ProfileUiState.Loaded)?.etudiant ?: return
        viewModelScope.launch {
            _uiState.value = ProfileUiState.Loaded(current, saving = true)
            try {
                val updated = current.copy(
                    filiere = filiere.ifBlank { null },
                    niveau = niveau.ifBlank { null }
                )
                val result = etudiantRepository.modifier(current.id, updated)
                _uiState.value = ProfileUiState.Loaded(result, saved = true)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Loaded(current, error = e.toUserMessage())
            }
        }
    }
}
'@

Set-Content -Path "$base\ui\profile\ProfileEditScreen.kt" -Encoding utf8 -Value @'
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
                            label = { Text("Filière") },
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
                                "Profil mis à jour ✓",
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
'@

# ============================================================
# 4. Mise à jour ViewModelFactory (ajout des 3 nouveaux ViewModels)
# ============================================================

Set-Content -Path "$base\ui\ViewModelFactory.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mycampus.android.data.AppContainer
import com.mycampus.android.ui.absences.AbsenceViewModel
import com.mycampus.android.ui.annonces.AnnonceViewModel
import com.mycampus.android.ui.auth.LoginViewModel
import com.mycampus.android.ui.auth.RegisterViewModel
import com.mycampus.android.ui.cours.CoursViewModel
import com.mycampus.android.ui.dashboard.DashboardViewModel
import com.mycampus.android.ui.notes.NoteViewModel
import com.mycampus.android.ui.notifications.NotificationViewModel
import com.mycampus.android.ui.profile.ProfileViewModel
import com.mycampus.android.ui.seances.SeanceViewModel

class ViewModelFactory(private val container: AppContainer) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) ->
                LoginViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->
                RegisterViewModel(container.authRepository) as T

            modelClass.isAssignableFrom(DashboardViewModel::class.java) ->
                DashboardViewModel(
                    container.authRepository,
                    container.etudiantRepository,
                    container.noteRepository
                ) as T

            modelClass.isAssignableFrom(CoursViewModel::class.java) ->
                CoursViewModel(container.coursRepository) as T

            modelClass.isAssignableFrom(AnnonceViewModel::class.java) ->
                AnnonceViewModel(container.annonceRepository) as T

            modelClass.isAssignableFrom(AbsenceViewModel::class.java) ->
                AbsenceViewModel(container.authRepository, container.absenceRepository) as T

            modelClass.isAssignableFrom(NotificationViewModel::class.java) ->
                NotificationViewModel(container.authRepository, container.notificationRepository) as T

            modelClass.isAssignableFrom(NoteViewModel::class.java) ->
                NoteViewModel(container.authRepository, container.noteRepository) as T

            modelClass.isAssignableFrom(SeanceViewModel::class.java) ->
                SeanceViewModel(
                    container.etudiantRepository,
                    container.coursRepository,
                    container.seanceRepository
                ) as T

            modelClass.isAssignableFrom(ProfileViewModel::class.java) ->
                ProfileViewModel(container.etudiantRepository) as T

            else -> throw IllegalArgumentException("ViewModel inconnu : ${modelClass.name}")
        }
    }
}
'@

# ============================================================
# 5. Mise à jour NavGraph (ajout des 3 routes)
# ============================================================

Set-Content -Path "$base\ui\navigation\NavGraph.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycampus.android.ui.ViewModelFactory
import com.mycampus.android.ui.absences.AbsenceListScreen
import com.mycampus.android.ui.annonces.AnnonceListScreen
import com.mycampus.android.ui.auth.LoginScreen
import com.mycampus.android.ui.auth.RegisterScreen
import com.mycampus.android.ui.cours.CoursListScreen
import com.mycampus.android.ui.dashboard.DashboardScreen
import com.mycampus.android.ui.notes.NoteListScreen
import com.mycampus.android.ui.notifications.NotificationListScreen
import com.mycampus.android.ui.profile.ProfileEditScreen
import com.mycampus.android.ui.seances.SeanceListScreen

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val COURS = "cours"
    const val ANNONCES = "annonces"
    const val ABSENCES = "absences"
    const val NOTIFICATIONS = "notifications"
    const val NOTES = "notes"
    const val SEANCES = "seances"
    const val PROFILE = "profile"
}

@Composable
fun MyCampusNavGraph(
    factory: ViewModelFactory,
    startDestination: String,
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.LOGIN) {
            LoginScreen(
                factory = factory,
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                factory = factory,
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                factory = factory,
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToCours = { navController.navigate(Routes.COURS) },
                onNavigateToAnnonces = { navController.navigate(Routes.ANNONCES) },
                onNavigateToAbsences = { navController.navigate(Routes.ABSENCES) },
                onNavigateToNotifications = { navController.navigate(Routes.NOTIFICATIONS) },
                onNavigateToNotes = { navController.navigate(Routes.NOTES) },
                onNavigateToSeances = { navController.navigate(Routes.SEANCES) },
                onNavigateToProfile = { navController.navigate(Routes.PROFILE) }
            )
        }

        composable(Routes.COURS) {
            CoursListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.ANNONCES) {
            AnnonceListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.ABSENCES) {
            AbsenceListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTIFICATIONS) {
            NotificationListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.NOTES) {
            NoteListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.SEANCES) {
            SeanceListScreen(factory = factory, onBack = { navController.popBackStack() })
        }

        composable(Routes.PROFILE) {
            ProfileEditScreen(factory = factory, onBack = { navController.popBackStack() })
        }
    }
}
'@

# ============================================================
# 6. Mise à jour DashboardScreen (ajout des 3 nouvelles tuiles)
# ============================================================

Set-Content -Path "$base\ui\dashboard\DashboardScreen.kt" -Encoding utf8 -Value @'
package com.mycampus.android.ui.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Announcement
import androidx.compose.material.icons.automirrored.filled.Logout
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
                        Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Déconnexion")
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
                        Button(onClick = { viewModel.load() }) { Text("Réessayer") }
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
                                state.etudiant.filiere?.let { Text("Filière : $it") }
                                state.etudiant.niveau?.let { Text("Niveau : $it") }

                                Spacer(Modifier.height(12.dp))
                                Divider()
                                Spacer(Modifier.height(12.dp))

                                val moyenne = state.moyenne
                                when (moyenne.statut) {
                                    "AUCUNE_NOTE" -> Text("Aucune note enregistrée pour le moment")
                                    else -> {
                                        Text(
                                            "Moyenne générale : ${moyenne.moyenne ?: "-"}/20",
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                        val couleur = if (moyenne.statut == "ADMIS")
                                            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                        Text(
                                            text = if (moyenne.statut == "ADMIS") "Admis(e)" else "Ajourné(e)",
                                            color = couleur,
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                        Text("${moyenne.nombreNotes} note(s) prise(s) en compte")
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(20.dp))
                        Text("Accès rapide", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(8.dp))

                        val items = listOf(
                            Triple("Cours", Icons.Default.MenuBook, onNavigateToCours),
                            Triple("Notes", Icons.Default.Grade, onNavigateToNotes),
                            Triple("Emploi du temps", Icons.Default.Schedule, onNavigateToSeances),
                            Triple("Absences", Icons.Default.EventBusy, onNavigateToAbsences),
                            Triple("Annonces", Icons.AutoMirrored.Filled.Announcement, onNavigateToAnnonces),
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
'@

Write-Host "Termine : Notes, Emploi du temps et Profil ajoutes avec succes." -ForegroundColor Green
Write-Host "Retourne dans Android Studio -> il devrait detecter les nouveaux fichiers et proposer un Sync Gradle." -ForegroundColor Cyan