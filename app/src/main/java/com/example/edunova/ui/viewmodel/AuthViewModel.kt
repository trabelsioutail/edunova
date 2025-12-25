package com.example.edunova.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edunova.data.model.AuthResponse
import com.example.edunova.data.model.User
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel d'authentification (Critère I.2 - Couche ViewModel)
 * Critère II.1 - Authentification & Critère II.2 - Gestion de Session
 * Utilise ViewModelScope pour les coroutines et LiveData pour l'observation
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    // Critère II.2 - Gestion de Session : Observer l'utilisateur connecté
    val authenticatedUser: LiveData<User?> = authRepository.getLoggedInUser()

    // États UI avec LiveData (Critère I.2)
    private val _loginState = MutableLiveData<NetworkResult<AuthResponse>?>()
    val loginState: LiveData<NetworkResult<AuthResponse>?> = _loginState

    private val _registerState = MutableLiveData<NetworkResult<AuthResponse>?>()
    val registerState: LiveData<NetworkResult<AuthResponse>?> = _registerState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /**
     * Critère II.1 - Authentification
     * Login avec gestion d'état via LiveData
     */
    fun login(email: String, password: String) {
        // Critère I.2 - ViewModelScope pour les coroutines
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authRepository.login(email.trim(), password)
            
            when (result) {
                is NetworkResult.Success -> {
                    _loginState.value = result
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    _loginState.value = null
                }
                is NetworkResult.Loading -> {
                    // Géré par _isLoading
                }
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Critère II.1 - Authentification
     * Register avec gestion d'état via LiveData
     */
    fun register(firstName: String, lastName: String, email: String, password: String) {
        // Critère I.2 - ViewModelScope pour les coroutines
        viewModelScope.launch {
            println("🎯 AuthViewModel.register() - Début")
            println("📝 Données: firstName=$firstName, lastName=$lastName, email=$email")
            
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authRepository.register(
                firstName.trim(), 
                lastName.trim(), 
                email.trim(), 
                password
            )
            
            println("📊 Résultat AuthRepository: $result")
            
            when (result) {
                is NetworkResult.Success -> {
                    println("✅ Inscription réussie dans ViewModel")
                    _registerState.value = result
                    _errorMessage.value = null
                }
                is NetworkResult.Error -> {
                    println("❌ Erreur inscription dans ViewModel: ${result.message}")
                    _errorMessage.value = result.message
                    _registerState.value = null
                }
                is NetworkResult.Loading -> {
                    println("⏳ Loading dans ViewModel")
                    // Géré par _isLoading
                }
            }
            
            _isLoading.value = false
            println("🏁 AuthViewModel.register() - Fin")
        }
    }

    /**
     * Critère II.2 - Gestion de Session
     * Déconnexion avec nettoyage d'état
     */
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val result = authRepository.logout()
            
            when (result) {
                is NetworkResult.Success -> {
                    clearAllStates()
                }
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                }
                is NetworkResult.Loading -> {
                    // Géré par _isLoading
                }
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Vérifier si l'utilisateur est connecté
     */
    fun checkAuthStatus() {
        viewModelScope.launch {
            val isLoggedIn = authRepository.isLoggedIn()
            if (!isLoggedIn) {
                clearAllStates()
            }
        }
    }

    /**
     * Refresh token
     */
    fun refreshToken() {
        viewModelScope.launch {
            val result = authRepository.refreshToken()
            
            when (result) {
                is NetworkResult.Error -> {
                    _errorMessage.value = result.message
                    // En cas d'erreur de refresh, déconnecter l'utilisateur
                    logout()
                }
                else -> {
                    // Token refreshed successfully
                }
            }
        }
    }

    /**
     * Nettoyer les messages d'erreur
     */
    fun clearError() { 
        _errorMessage.value = null 
    }

    /**
     * Nettoyer tous les états
     */
    fun clearAllStates() {
        _loginState.value = null
        _registerState.value = null
        _errorMessage.value = null
    }

    /**
     * Nettoyer toutes les sessions au démarrage
     */
    fun clearAllSessions() {
        viewModelScope.launch {
            authRepository.clearAllSessions()
            clearAllStates()
        }
    }

    /**
     * Tester la connexion à la base de données
     */
    fun testDatabase() {
        viewModelScope.launch {
            println("🧪 Test de la base de données depuis ViewModel...")
            val result = authRepository.testDatabaseConnection()
            when (result) {
                is NetworkResult.Success -> {
                    println("✅ Test DB réussi depuis ViewModel")
                }
                is NetworkResult.Error -> {
                    println("❌ Test DB échoué depuis ViewModel: ${result.message}")
                    _errorMessage.value = "Test DB: ${result.message}"
                }
                is NetworkResult.Loading -> {
                    println("⏳ Test DB en cours...")
                }
            }
        }
    }

    /**
     * Vérifier les utilisateurs en base de données
     */
    fun checkUsersInDatabase() {
        viewModelScope.launch {
            println("🔍 Vérification des utilisateurs en base...")
            val users = authRepository.getAllUsers()
            println("📊 ${users.size} utilisateur(s) trouvé(s) en base")
        }
    }
}