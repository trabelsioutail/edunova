package com.example.edunova.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.edunova.data.model.Course
import com.example.edunova.data.model.User
import com.example.edunova.ui.viewmodel.AuthViewModel
import com.example.edunova.ui.viewmodel.CourseViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    courseViewModel: CourseViewModel = hiltViewModel(),
    onLogout: () -> Unit
) {
    // Observer les états
    val authenticatedUser by authViewModel.authenticatedUser.observeAsState()
    val courses by courseViewModel.courses.observeAsState(emptyList())
    val isLoading by courseViewModel.isLoading.observeAsState(false)
    val errorMessage by courseViewModel.errorMessage.observeAsState()

    // Charger les cours au démarrage
    LaunchedEffect(authenticatedUser) {
        if (authenticatedUser != null) {
            courseViewModel.fetchCourses()
        }
    }

    // Rediriger si pas connecté
    LaunchedEffect(authenticatedUser) {
        if (authenticatedUser == null) {
            onLogout()
        }
    }

    // ✅ Solution : Utiliser let pour éviter le smart cast impossible
    authenticatedUser?.let { user ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Barre de titre
            TopAppBar(
                title = { 
                    Text("EduNova - ${user.firstName} ${user.lastName}") // ✅ user au lieu de authenticatedUser
                },
                actions = {
                    IconButton(
                        onClick = { courseViewModel.fetchCourses(forceRefresh = true) }
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Actualiser")
                    }
                    IconButton(
                        onClick = { 
                            authViewModel.logout()
                            onLogout()
                        }
                    ) {
                        // ✅ Icône AutoMirrored pour éviter la dépréciation
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Déconnexion")
                    }
                }
            )

            // Contenu principal
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Informations utilisateur - ✅ Passer user au lieu de authenticatedUser
                UserInfoCard(user = user)

                Spacer(modifier = Modifier.height(16.dp))

                // Section des cours
                Text(
                    text = "Mes Cours",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Gestion des erreurs
                errorMessage?.let { error ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.weight(1f)
                            )
                            TextButton(onClick = { courseViewModel.clearError() }) {
                                Text("OK")
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Liste des cours
                when {
                    isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    courses.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Aucun cours disponible")
                                Spacer(modifier = Modifier.height(8.dp))
                                TextButton(
                                    onClick = { courseViewModel.fetchCourses(forceRefresh = true) }
                                ) {
                                    Text("Actualiser")
                                }
                            }
                        }
                    }
                    else -> {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(courses) { course ->
                                CourseCard(course = course)
                            }
                        }
                    }
                }
            }
        }
    } ?: run {
        // Écran de chargement si pas d'utilisateur connecté
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Chargement...")
            }
        }
    }
}

@Composable
private fun UserInfoCard(user: User) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Profil Utilisateur",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text("Nom: ${user.firstName} ${user.lastName}")
            Text("Email: ${user.email}")
            Text("Rôle: ${user.role}")
            Text("Statut: ${if (user.isVerified) "Vérifié" else "Non vérifié"}")
        }
    }
}

@Composable
private fun CourseCard(course: Course) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = course.title,
                style = MaterialTheme.typography.headlineSmall
            )
            
            course.description?.let { description ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Enseignant ID: ${course.teacherId}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Créé le: ${course.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}