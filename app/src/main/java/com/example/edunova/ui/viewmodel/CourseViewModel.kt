package com.example.edunova.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edunova.data.model.Course
import com.example.edunova.data.model.CreateCourseRequest
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.repository.AuthRepository
import com.example.edunova.data.repository.CourseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel des cours (Critère I.2 - Couche ViewModel)
 * Critère II.3 - CRUD Manipulation des Données
 * Utilise ViewModelScope et LiveData pour l'observation réactive
 */
@HiltViewModel
class CourseViewModel @Inject constructor(
    private val courseRepository: CourseRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // Critère I.3 - Source de Vérité Unique : Observer depuis Room
    val courses: LiveData<List<Course>> = courseRepository.getAllCourses()

    // États UI avec LiveData (Critère I.2)
    private val _courseState = MutableLiveData<NetworkResult<Course>?>()
    val courseState: LiveData<NetworkResult<Course>?> = _courseState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _operationSuccess = MutableLiveData<String?>()
    val operationSuccess: LiveData<String?> = _operationSuccess

    /**
     * Critère II.3 - CRUD (GET)
     * Récupérer tous les cours avec gestion du cache
     */
    fun fetchCourses(forceRefresh: Boolean = false) {
        // Critère I.2 - ViewModelScope pour les coroutines
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = courseRepository.fetchCourses(token, forceRefresh)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _operationSuccess.value = "Cours chargés avec succès"
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Critère II.3 - CRUD (GET by ID)
     * Récupérer un cours par ID
     */
    fun getCourseById(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = courseRepository.getCourseById(courseId, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _courseState.value = result
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Critère II.3 - CRUD (POST)
     * Créer un nouveau cours
     */
    fun createCourse(title: String, description: String?, teacherId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val request = CreateCourseRequest(title, description, teacherId)
                val result = courseRepository.createCourse(request, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _courseState.value = result
                        _operationSuccess.value = "Cours créé avec succès"
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Critère II.3 - CRUD (PUT)
     * Mettre à jour un cours
     */
    fun updateCourse(courseId: Int, title: String, description: String?, teacherId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val request = CreateCourseRequest(title, description, teacherId)
                val result = courseRepository.updateCourse(courseId, request, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _courseState.value = result
                        _operationSuccess.value = "Cours mis à jour avec succès"
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Critère II.3 - CRUD (DELETE)
     * Supprimer un cours
     */
    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = courseRepository.deleteCourse(courseId, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _operationSuccess.value = "Cours supprimé avec succès"
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Observer les cours d'un enseignant
     */
    fun getCoursesByTeacher(teacherId: Int): LiveData<List<Course>> {
        return courseRepository.getCoursesByTeacher(teacherId)
    }

    /**
     * Récupérer les cours d'un enseignant depuis l'API
     */
    fun fetchCoursesByTeacher(teacherId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = courseRepository.fetchCoursesByTeacher(teacherId, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _operationSuccess.value = "Cours de l'enseignant chargés"
                    }
                    is NetworkResult.Error -> {
                        _errorMessage.value = result.message
                    }
                    is NetworkResult.Loading -> {
                        // Géré par _isLoading
                    }
                }
            } else {
                _errorMessage.value = "Token d'authentification manquant"
            }
            
            _isLoading.value = false
        }
    }

    /**
     * Nettoyer les messages
     */
    fun clearError() { 
        _errorMessage.value = null 
    }

    fun clearSuccess() {
        _operationSuccess.value = null
    }

    fun clearCourseState() {
        _courseState.value = null
    }
}