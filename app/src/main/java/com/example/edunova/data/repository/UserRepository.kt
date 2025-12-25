package com.example.edunova.data.repository

import com.example.edunova.data.local.dao.UserDao
import com.example.edunova.data.mapper.UserMapper
import com.example.edunova.data.model.*
import com.example.edunova.data.remote.ApiService
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.remote.safeApiCallWithWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository des utilisateurs (Critère I.3 - Source de Vérité Unique)
 * CRUD complet pour la gestion des utilisateurs
 */
@Singleton
class UserRepository @Inject constructor(
    private val apiService: ApiService,
    private val userDao: UserDao
) {

    suspend fun getUsers(token: String): NetworkResult<List<User>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.getUsers("Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val users = result.data
                        // Optionnel : sauvegarder en cache
                        val userEntities = UserMapper.toEntityList(users)
                        // userDao.insertUsers(userEntities) // Si vous avez cette méthode
                        
                        NetworkResult.Success(users)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de chargement des utilisateurs: ${e.message}")
            }
        }
    }

    suspend fun getUserById(userId: Int, token: String): NetworkResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.getUserById(userId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val user = result.data
                        // Optionnel : sauvegarder en cache
                        val userEntity = UserMapper.toEntity(user)
                        userDao.insertUser(userEntity)
                        
                        NetworkResult.Success(user)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de chargement de l'utilisateur: ${e.message}")
            }
        }
    }

    suspend fun updateUser(userId: Int, user: User, token: String): NetworkResult<User> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.updateUser(userId, user, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val updatedUser = result.data
                        // Mettre à jour le cache
                        val userEntity = UserMapper.toEntity(updatedUser)
                        userDao.updateUser(userEntity)
                        
                        NetworkResult.Success(updatedUser)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de mise à jour de l'utilisateur: ${e.message}")
            }
        }
    }

    suspend fun deleteUser(userId: Int, token: String): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.deleteUser(userId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        // Supprimer du cache
                        val userEntity = userDao.getUserById(userId)
                        userEntity?.let { userDao.deleteUser(it) }
                        
                        NetworkResult.Success(true)
                    }
                    is NetworkResult.Error -> NetworkResult.Error(result.message)
                    is NetworkResult.Loading -> NetworkResult.Error("Opération en cours")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de suppression de l'utilisateur: ${e.message}")
            }
        }
    }
}