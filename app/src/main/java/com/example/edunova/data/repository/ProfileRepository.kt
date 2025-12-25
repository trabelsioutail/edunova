package com.example.edunova.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.edunova.data.local.dao.ProfileDao
import com.example.edunova.data.mapper.ProfileMapper
import com.example.edunova.data.model.*
import com.example.edunova.data.remote.ApiService
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.remote.safeApiCallWithWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository des profils (Critère I.3 - Source de Vérité Unique)
 * CRUD complet pour la gestion des profils
 */
@Singleton
class ProfileRepository @Inject constructor(
    private val apiService: ApiService,
    private val profileDao: ProfileDao
) {

    /**
     * Observer tous les profils depuis Room
     */
    fun getAllProfiles(): LiveData<List<Profile>> {
        return profileDao.getAllProfiles().map { entities ->
            ProfileMapper.toModelList(entities)
        }
    }

    /**
     * Récupérer le profil de l'utilisateur connecté
     */
    suspend fun getProfile(token: String): NetworkResult<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.getProfile("Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val profile = result.data
                        // Sauvegarder en cache
                        val profileEntity = ProfileMapper.toEntity(profile)
                        profileDao.insertProfile(profileEntity)
                        
                        NetworkResult.Success(profile)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de chargement du profil: ${e.message}")
            }
        }
    }

    /**
     * Mettre à jour le profil
     */
    suspend fun updateProfile(
        request: UpdateProfileRequest,
        token: String
    ): NetworkResult<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.updateProfile(request, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val profile = result.data
                        // Mettre à jour le cache
                        val profileEntity = ProfileMapper.toEntity(profile)
                        profileDao.updateProfile(profileEntity)
                        
                        NetworkResult.Success(profile)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de mise à jour du profil: ${e.message}")
            }
        }
    }

    /**
     * Récupérer tous les profils (pour admin)
     */
    suspend fun fetchAllProfiles(token: String): NetworkResult<List<Profile>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.getAllProfiles("Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val profiles = result.data
                        // Sauvegarder en cache
                        val profileEntities = ProfileMapper.toEntityList(profiles)
                        profileDao.deleteAllProfiles()
                        profileDao.insertProfiles(profileEntities)
                        
                        NetworkResult.Success(profiles)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                // Retourner le cache en cas d'erreur
                val cachedProfiles = profileDao.getAllProfilesSync()
                if (cachedProfiles.isNotEmpty()) {
                    NetworkResult.Success(ProfileMapper.toModelList(cachedProfiles))
                } else {
                    NetworkResult.Error("Erreur de chargement des profils: ${e.message}")
                }
            }
        }
    }

    /**
     * Récupérer un profil par ID
     */
    suspend fun getProfileById(profileId: Long, token: String): NetworkResult<Profile> {
        return withContext(Dispatchers.IO) {
            try {
                // Vérifier le cache d'abord
                val cachedProfile = profileDao.getProfileById(profileId)
                if (cachedProfile != null) {
                    return@withContext NetworkResult.Success(ProfileMapper.toModel(cachedProfile))
                }

                // Appeler l'API
                val result = safeApiCallWithWrapper { 
                    apiService.getProfileById(profileId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val profile = result.data
                        // Sauvegarder en cache
                        val profileEntity = ProfileMapper.toEntity(profile)
                        profileDao.insertProfile(profileEntity)
                        
                        NetworkResult.Success(profile)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de chargement du profil: ${e.message}")
            }
        }
    }

    /**
     * Observer les profils par rôle
     */
    fun getProfilesByRole(role: String): LiveData<List<Profile>> {
        return profileDao.getProfilesByRole(role).map { entities ->
            ProfileMapper.toModelList(entities)
        }
    }
}