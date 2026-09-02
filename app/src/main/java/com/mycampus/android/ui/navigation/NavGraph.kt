package com.mycampus.android.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mycampus.android.ui.ViewModelFactory
import com.mycampus.android.ui.absences.AbsenceListScreen
import com.mycampus.android.ui.admin.AdminDashboardScreen
import com.mycampus.android.ui.admin.cours.AdminCoursListScreen
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

    const val ADMIN_DASHBOARD = "admin_dashboard"
    const val ADMIN_COURS = "admin_cours"
    const val ADMIN_ENSEIGNANTS = "admin_enseignants"
    const val ADMIN_ETUDIANTS = "admin_etudiants"
    const val ADMIN_NOTES = "admin_notes"
    const val ADMIN_ABSENCES = "admin_absences"
    const val ADMIN_ANNONCES = "admin_annonces"
    const val ADMIN_SEANCES = "admin_seances"
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
                onLoginSuccess = { role ->
                    val destination = if (role == "ADMIN") Routes.ADMIN_DASHBOARD else Routes.DASHBOARD
                    navController.navigate(destination) {
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

        composable(Routes.ADMIN_DASHBOARD) {
            AdminDashboardScreen(
                onNavigateToCours = { navController.navigate(Routes.ADMIN_COURS) },
                onNavigateToEnseignants = { navController.navigate(Routes.ADMIN_ENSEIGNANTS) },
                onNavigateToEtudiants = { navController.navigate(Routes.ADMIN_ETUDIANTS) },
                onNavigateToNotes = { navController.navigate(Routes.ADMIN_NOTES) },
                onNavigateToAbsences = { navController.navigate(Routes.ADMIN_ABSENCES) },
                onNavigateToAnnonces = { navController.navigate(Routes.ADMIN_ANNONCES) },
                onNavigateToSeances = { navController.navigate(Routes.ADMIN_SEANCES) },
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN_COURS) {
            AdminCoursListScreen(factory = factory, onBack = { navController.popBackStack() })
        }
    }
}
