package com.example.edunova.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edunova.ui.screen.HomeScreen
import com.example.edunova.ui.theme.EdunovaTheme
import com.example.edunova.ui.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * MainActivity respectant les critères du professeur
 * Critère I.1 - Séparation des Couches : Activity observe seulement les ViewModels
 * Critère II.2 - Gestion de Session : Utilise authenticatedUser.observe
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Critère I.2 - ViewModels injectés via Hilt
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            EdunovaTheme {
                MainScreen()
            }
        }
    }

    @Composable
    private fun MainScreen() {
        // Critère I.1 - Activity observe seulement les LiveData du ViewModel
        val authenticatedUser by authViewModel.authenticatedUser.observeAsState()

        // ✅ Utiliser une variable pour éviter les redirections multiples
        var hasRedirected by remember { mutableStateOf(false) }

        // Critère II.2 - Gestion de Session : Conditionner l'affichage selon l'utilisateur connecté
        LaunchedEffect(authenticatedUser, hasRedirected) {
            if (authenticatedUser == null && !hasRedirected) {
                // Utilisateur non connecté - rediriger vers login UNE SEULE FOIS
                hasRedirected = true
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finish()
            }
        }

        // Si utilisateur connecté, afficher HomeScreen
        if (authenticatedUser != null) {
            HomeScreen(
                onLogout = {
                    // Rediriger vers LoginActivity après déconnexion
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    finish()
                }
            )
        } else {
            // ✅ Afficher un écran de chargement au lieu de rien
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Vérification de la session...")
                }
            }
        }
    }
}