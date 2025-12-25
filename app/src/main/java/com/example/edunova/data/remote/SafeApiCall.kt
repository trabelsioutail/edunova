package com.example.edunova.data.remote

import com.example.edunova.data.model.ApiResponse
import retrofit2.Response
import java.io.IOException

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { body ->
                NetworkResult.Success(body)
            } ?: NetworkResult.Error("Réponse vide du serveur")
        } else {
            NetworkResult.Error("Erreur ${response.code()}: ${response.message()}")
        }
    } catch (e: IOException) {
        NetworkResult.Error("Erreur de connexion réseau: ${e.message}")
    } catch (e: Exception) {
        NetworkResult.Error("Erreur inattendue: ${e.message}")
    }
}

// Nouvelle fonction pour les réponses API wrappées
suspend fun <T> safeApiCallWithWrapper(apiCall: suspend () -> Response<ApiResponse<T>>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let { apiResponse ->
                if (apiResponse.success && apiResponse.data != null) {
                    NetworkResult.Success(apiResponse.data)
                } else {
                    NetworkResult.Error(apiResponse.message ?: apiResponse.error ?: "Erreur inconnue")
                }
            } ?: NetworkResult.Error("Réponse vide du serveur")
        } else {
            NetworkResult.Error("Erreur ${response.code()}: ${response.message()}")
        }
    } catch (e: IOException) {
        NetworkResult.Error("Erreur de connexion réseau: ${e.message}")
    } catch (e: Exception) {
        NetworkResult.Error("Erreur inattendue: ${e.message}")
    }
}