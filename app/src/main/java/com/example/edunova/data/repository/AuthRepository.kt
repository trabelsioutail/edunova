package com.example.edunova.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.edunova.data.local.dao.UserDao
import com.example.edunova.data.local.entity.UserEntity
import com.example.edunova.data.mapper.UserMapper
import com.example.edunova.data.model.*
import com.example.edunova.data.remote.ApiService
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.remote.safeApiCall
import com.example.edunova.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository d'authentification (Critère I.3 - Source de Vérité Unique)
 * Gère l'authentification et la session utilisateur
 * Critère II.1 - Authentification & Critère II.2 - Gestion de Session
 */
@Singleton
class AuthRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    /**
     * Critère II.2 - Gestion de Session
     * Observer l'utilisateur connecté depuis Room Database
     */
    fun getLoggedInUser(): LiveData<User?> {
        return userDao.getLoggedInUser().map { entity ->
            entity?.let { 
                println("🔍 Utilisateur connecté trouvé: ${entity.email}")
                UserMapper.toModel(it) 
            }
        }
    }

    /**
     * Nettoyer toutes les sessions au démarrage de l'app
     */
    suspend fun clearAllSessions() {
        withContext(Dispatchers.IO) {
            try {
                println("🧹 Nettoyage des sessions au démarrage (pas des utilisateurs)")
                // ✅ Ne supprimer que les sessions, pas les utilisateurs
                userDao.logoutAllUsers() // Met isLoggedIn = false et authToken = null
                println("✅ Sessions nettoyées, utilisateurs conservés")
            } catch (e: Exception) {
                println("❌ Erreur lors du nettoyage des sessions: ${e.message}")
            }
        }
    }

    /**
     * Critère II.1 - Authentification
     * Login avec gestion de session dans Room
     */
    suspend fun login(email: String, password: String): NetworkResult<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 AuthRepository.login() - Début connexion")
                println("📝 Email: $email")
                
                // ✅ Mode hors ligne pour les tests
                if (Constants.OFFLINE_MODE) {
                    println("🔧 Mode hors ligne activé - Simulation de connexion")
                    return@withContext simulateLogin(email, password)
                }
                
                val result = safeApiCall { 
                    apiService.login(AuthRequest(email = email, password = password)) 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val authResponse = result.data
                        if (authResponse.success && authResponse.user != null && authResponse.token != null) {
                            // Critère II.2 - Stockage du token dans Room
                            val userEntity = UserMapper.toEntity(
                                user = authResponse.user,
                                authToken = authResponse.token,
                                isLoggedIn = true
                            )
                            
                            // Déconnecter tous les autres utilisateurs
                            userDao.logoutAllUsers()
                            // Sauvegarder l'utilisateur connecté
                            userDao.insertUser(userEntity)
                        }
                        result
                    }
                    is NetworkResult.Error -> {
                        println("❌ Erreur API: ${result.message}")
                        
                        // ✅ Fallback en mode hors ligne si erreur API
                        if (Constants.MOCK_API_RESPONSES) {
                            println("🔧 Fallback mode hors ligne après erreur API")
                            return@withContext simulateLogin(email, password)
                        }
                        
                        result
                    }
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                println("💥 Exception dans login(): ${e.message}")
                
                // ✅ Fallback en mode hors ligne si exception
                if (Constants.MOCK_API_RESPONSES) {
                    println("🔧 Fallback mode hors ligne après exception")
                    return@withContext simulateLogin(email, password)
                }
                
                NetworkResult.Error("Erreur de connexion: ${e.message}")
            }
        }
    }
    
    /**
     * Simulation de connexion pour le mode hors ligne
     */
    private suspend fun simulateLogin(email: String, password: String): NetworkResult<AuthResponse> {
        println("🎭 Simulation de connexion hors ligne")
        
        // Vérifier si l'utilisateur existe en local
        val existingUser = userDao.getUserByEmail(email)
        
        if (existingUser != null) {
            println("✅ Utilisateur trouvé en local: $email")
            
            // Simuler la connexion réussie
            val user = UserMapper.toModel(existingUser)
            val token = "offline-login-token-${System.currentTimeMillis()}"
            
            // Mettre à jour le token et le statut de connexion
            val updatedEntity = existingUser.copy(
                authToken = token,
                isLoggedIn = true
            )
            
            userDao.logoutAllUsers()
            userDao.insertUser(updatedEntity)
            
            println("✅ Connexion hors ligne réussie: $email")
            
            return NetworkResult.Success(AuthResponse(
                success = true,
                message = "Connexion hors ligne réussie",
                user = user,
                token = token
            ))
        } else {
            // Vérifier les identifiants de test
            if (email == Constants.MockData.TEST_EMAIL && password == Constants.MockData.TEST_PASSWORD) {
                println("✅ Connexion avec identifiants de test")
                
                val testUser = User(
                    id = 1,
                    firstName = "Test",
                    lastName = "User",
                    email = Constants.MockData.TEST_EMAIL,
                    role = Constants.UserRoles.STUDENT,
                    isVerified = true,
                    createdAt = java.time.LocalDateTime.now().toString(),
                    updatedAt = java.time.LocalDateTime.now().toString()
                )
                
                val userEntity = UserMapper.toEntity(
                    user = testUser,
                    authToken = Constants.MockData.TEST_TOKEN,
                    isLoggedIn = true
                )
                
                userDao.logoutAllUsers()
                userDao.insertUser(userEntity)
                
                return NetworkResult.Success(AuthResponse(
                    success = true,
                    message = "Connexion test réussie",
                    user = testUser,
                    token = Constants.MockData.TEST_TOKEN
                ))
            } else {
                println("❌ Identifiants incorrects en mode hors ligne")
                return NetworkResult.Success(AuthResponse(
                    success = false,
                    message = "Email ou mot de passe incorrect"
                ))
            }
        }
    }

    /**
     * Critère II.1 - Authentification
     * Register avec sauvegarde automatique
     */
    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 AuthRepository.register() - Début inscription")
                println("📝 Données: firstName=$firstName, lastName=$lastName, email=$email")
                
                // ✅ Mode hors ligne pour les tests
                if (Constants.OFFLINE_MODE) {
                    println("🔧 Mode hors ligne activé - Simulation d'inscription")
                    return@withContext simulateRegister(firstName, lastName, email, password)
                }
                
                val result = safeApiCall {
                    apiService.register(
                        AuthRequest(
                            first_name = firstName,
                            last_name = lastName,
                            email = email,
                            password = password
                        )
                    )
                }
                
                println("📡 Réponse API: $result")
                
                when (result) {
                    is NetworkResult.Success -> {
                        val authResponse = result.data
                        println("✅ Réponse API Success: success=${authResponse.success}, user=${authResponse.user}, token=${authResponse.token}")
                        
                        if (authResponse.success && authResponse.user != null && authResponse.token != null) {
                            println("💾 Sauvegarde en cours dans Room Database...")
                            
                            // Critère II.2 - Stockage du token dans Room
                            val userEntity = UserMapper.toEntity(
                                user = authResponse.user,
                                authToken = authResponse.token,
                                isLoggedIn = true
                            )
                            
                            println("🔄 UserEntity créé: $userEntity")
                            
                            // Déconnecter tous les autres utilisateurs
                            userDao.logoutAllUsers()
                            println("🚪 Tous les utilisateurs déconnectés")
                            
                            // Sauvegarder l'utilisateur connecté
                            userDao.insertUser(userEntity)
                            println("✅ Utilisateur sauvegardé dans Room Database")
                            
                            // Vérifier la sauvegarde
                            val savedUser = userDao.getLoggedInUserSync()
                            println("🔍 Vérification: utilisateur sauvegardé = $savedUser")
                        } else {
                            println("❌ Réponse API invalide: success=${authResponse.success}, user=${authResponse.user}, token=${authResponse.token}")
                        }
                        result
                    }
                    is NetworkResult.Error -> {
                        println("❌ Erreur API: ${result.message}")
                        
                        // ✅ Fallback en mode hors ligne si erreur API
                        if (Constants.MOCK_API_RESPONSES) {
                            println("🔧 Fallback mode hors ligne après erreur API")
                            return@withContext simulateRegister(firstName, lastName, email, password)
                        }
                        
                        result
                    }
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                println("💥 Exception dans register(): ${e.message}")
                e.printStackTrace()
                
                // ✅ Fallback en mode hors ligne si exception
                if (Constants.MOCK_API_RESPONSES) {
                    println("🔧 Fallback mode hors ligne après exception")
                    return@withContext simulateRegister(firstName, lastName, email, password)
                }
                
                NetworkResult.Error("Erreur d'inscription: ${e.message}")
            }
        }
    }
    
    /**
     * Simulation d'inscription pour le mode hors ligne
     */
    private suspend fun simulateRegister(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): NetworkResult<AuthResponse> {
        println("🎭 Simulation d'inscription hors ligne")
        
        try {
            // Vérifier si l'utilisateur existe déjà
            println("🔍 Vérification de l'email existant: $email")
            val existingUser = userDao.getUserByEmail(email)
            if (existingUser != null) {
                println("❌ Email déjà utilisé en local: $email")
                return NetworkResult.Success(AuthResponse(
                    success = false,
                    message = "Email déjà utilisé"
                ))
            }
            println("✅ Email disponible: $email")
            
            // Créer un nouvel utilisateur
            val newUser = User(
                id = System.currentTimeMillis().toInt(),
                firstName = firstName,
                lastName = lastName,
                email = email,
                role = Constants.UserRoles.STUDENT,
                isVerified = true,
                createdAt = java.time.LocalDateTime.now().toString(),
                updatedAt = java.time.LocalDateTime.now().toString()
            )
            println("👤 Nouvel utilisateur créé: ${newUser.email} (ID: ${newUser.id})")
            
            val token = "offline-token-${System.currentTimeMillis()}"
            println("🔑 Token généré: $token")
            
            // Sauvegarder en Room
            val userEntity = UserMapper.toEntity(
                user = newUser,
                authToken = token,
                isLoggedIn = true
            )
            println("🔄 UserEntity créé: $userEntity")
            
            // Déconnecter les autres utilisateurs (sans les supprimer)
            println("🚪 Déconnexion des autres utilisateurs...")
            userDao.logoutAllUsers()
            
            // Insérer le nouvel utilisateur
            println("💾 Insertion de l'utilisateur en base...")
            userDao.insertUser(userEntity)
            println("✅ Utilisateur inséré avec succès")
            
            // Vérifier l'insertion
            println("🔍 Vérification de l'insertion...")
            val savedUser = userDao.getUserByEmail(email)
            if (savedUser != null) {
                println("✅ Utilisateur trouvé en base: ${savedUser.email} (ID: ${savedUser.id}, connecté: ${savedUser.isLoggedIn})")
            } else {
                println("❌ Utilisateur non trouvé en base après insertion!")
                return NetworkResult.Error("Erreur de sauvegarde en base de données")
            }
            
            // Vérifier tous les utilisateurs
            val allUsers = getAllUsers()
            println("📊 Total utilisateurs en base: ${allUsers.size}")
            
            println("✅ Inscription hors ligne réussie: ${newUser.email}")
            
            return NetworkResult.Success(AuthResponse(
                success = true,
                message = "Inscription hors ligne réussie",
                user = newUser,
                token = token
            ))
            
        } catch (e: Exception) {
            println("💥 Exception dans simulateRegister: ${e.message}")
            e.printStackTrace()
            return NetworkResult.Error("Erreur lors de l'inscription: ${e.message}")
        }
    }

    /**
     * Critère II.2 - Gestion de Session
     * Logout avec nettoyage de la session
     */
    suspend fun logout(): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                // Récupérer le token pour l'API
                val currentUser = userDao.getLoggedInUserSync()
                currentUser?.authToken?.let { token ->
                    // Appeler l'API de déconnexion (optionnel)
                    safeApiCall { apiService.logout("Bearer $token") }
                }
                
                // Nettoyer la session locale
                userDao.logoutAllUsers()
                NetworkResult.Success(true)
            } catch (e: Exception) {
                // Même en cas d'erreur API, nettoyer la session locale
                userDao.logoutAllUsers()
                NetworkResult.Success(true)
            }
        }
    }

    /**
     * Critère II.2 - Gestion de Session
     * Vérifier si l'utilisateur est connecté
     */
    suspend fun isLoggedIn(): Boolean {
        return withContext(Dispatchers.IO) {
            userDao.getLoggedInUserSync() != null
        }
    }

    /**
     * Critère II.2 - Gestion de Session
     * Récupérer le token d'authentification
     */
    suspend fun getAuthToken(): String? {
        return withContext(Dispatchers.IO) {
            userDao.getLoggedInUserSync()?.authToken
        }
    }

    /**
     * Vérifier les utilisateurs en base de données
     */
    suspend fun getAllUsers(): List<UserEntity> {
        return withContext(Dispatchers.IO) {
            try {
                val users = userDao.getAllUsers()
                println("🔍 Utilisateurs en base: ${users.size}")
                users.forEach { user ->
                    println("   - ${user.email} (ID: ${user.id}, connecté: ${user.isLoggedIn})")
                }
                users
            } catch (e: Exception) {
                println("❌ Erreur lors de la récupération des utilisateurs: ${e.message}")
                emptyList()
            }
        }
    }
    suspend fun refreshToken(): NetworkResult<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val currentUser = userDao.getLoggedInUserSync()
                val token = currentUser?.authToken
                
                if (token != null) {
                    val result = safeApiCall { apiService.refreshToken("Bearer $token") }
                    
                    when (result) {
                        is NetworkResult.Success -> {
                            val authResponse = result.data
                            if (authResponse.success && authResponse.token != null) {
                                // Mettre à jour le token
                                userDao.updateAuthToken(currentUser.id, authResponse.token)
                            }
                            result
                        }
                        is NetworkResult.Error -> result
                        is NetworkResult.Loading -> result
                    }
                } else {
                    NetworkResult.Error("Aucun token disponible")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de refresh token: ${e.message}")
            }
        }
    }

    /**
     * Fonction de test pour vérifier la base de données
     */
    suspend fun testDatabaseConnection(): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                println("🧪 Test de connexion à la base de données...")
                
                // Test d'insertion d'un utilisateur fictif
                val testUser = UserEntity(
                    id = 999,
                    firstName = "Test",
                    lastName = "User",
                    email = "test@example.com",
                    role = "etudiant",
                    isVerified = true,
                    authToken = "test-token",
                    isLoggedIn = true,
                    createdAt = "2024-01-01 00:00:00",
                    updatedAt = "2024-01-01 00:00:00"
                )
                
                // Insérer l'utilisateur de test
                userDao.insertUser(testUser)
                println("✅ Utilisateur de test inséré")
                
                // Vérifier l'insertion
                val retrievedUser = userDao.getUserById(999)
                println("🔍 Utilisateur récupéré: $retrievedUser")
                
                if (retrievedUser != null) {
                    println("✅ Base de données fonctionne correctement")
                    
                    // Nettoyer l'utilisateur de test
                    userDao.deleteUser(testUser)
                    println("🧹 Utilisateur de test supprimé")
                    
                    NetworkResult.Success(true)
                } else {
                    println("❌ Problème avec la base de données")
                    NetworkResult.Error("Impossible de récupérer l'utilisateur de test")
                }
            } catch (e: Exception) {
                println("💥 Erreur lors du test de la base de données: ${e.message}")
                e.printStackTrace()
                NetworkResult.Error("Erreur de test DB: ${e.message}")
            }
        }
    }
}