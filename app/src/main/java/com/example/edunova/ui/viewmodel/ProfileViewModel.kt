package com.example.edunova.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edunova.data.model.Profile
import com.example.edunova.data.model.UpdateProfileRequest
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.repository.AuthRepository
import com.example.edunova.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel des profils (Critère I.2 - Couche ViewModel)
 * Utilise ViewModelScope et LiveData pour l'observation réactive
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // États UI avec LiveData (Critère I.2)
    private val _profileState = MutableLiveData<NetworkResult<Profile>?>()
    val profileState: LiveData<NetworkResult<Profile>?> = _profileState

    private val _profilesState = MutableLiveData<NetworkResult<List<Profile>>?>()
    val profilesState: LiveData<NetworkResult<List<Profile>>?> = _profilesState

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    fun clearError() { _errorMessage.value = null }

    // Récupérer le profil de l'utilisateur connecté
    fun getProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = profileRepository.getProfile(token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _profileState.value = result
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

    // Mettre à jour le profil
    fun updateProfile(
        firstName: String?,
        lastName: String?,
        phone: String?,
        address: String?,
        age: Int?,
        profileImage: String?,
        level: String?,
        enrollmentYear: Int?,
        specialty: String?,
        yearsExperience: Int?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val request = UpdateProfileRequest(
                    firstName = firstName,
                    lastName = lastName,
                    phone = phone,
                    address = address,
                    age = age,
                    profileImage = profileImage,
                    level = level,
                    enrollmentYear = enrollmentYear,
                    specialty = specialty,
                    yearsExperience = yearsExperience
                )
                
                val result = profileRepository.updateProfile(request, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _profileState.value = result
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

    // Récupérer tous les profils (pour admin)
    fun getAllProfiles() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = profileRepository.fetchAllProfiles(token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _profilesState.value = result
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

    // Récupérer un profil par ID
    fun getProfileById(profileId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val token = authRepository.getAuthToken()
            if (token != null) {
                val result = profileRepository.getProfileById(profileId, token)
                
                when (result) {
                    is NetworkResult.Success -> {
                        _profileState.value = result
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
}