package com.mycampus.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.mycampus.android.ui.ViewModelFactory
import com.mycampus.android.ui.navigation.MyCampusNavGraph
import com.mycampus.android.ui.navigation.Routes
import com.mycampus.android.ui.theme.MyCampusTheme
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val container = (application as MyCampusApplication).container
        val factory = ViewModelFactory(container)

        // Vérifie si un token existe déjà pour sauter directement au dashboard.
        // runBlocking est acceptable ici : lecture DataStore quasi instantanée, une seule fois au démarrage.
        val startDestination = runBlocking {
            if (container.tokenManager.getTokenOnce() != null) Routes.DASHBOARD else Routes.LOGIN
        }

        setContent {
            MyCampusTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MyCampusNavGraph(factory = factory, startDestination = startDestination)
                }
            }
        }
    }
}
