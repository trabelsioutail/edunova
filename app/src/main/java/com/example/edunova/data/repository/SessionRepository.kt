package com.example.edunova.data.repository

import com.example.edunova.data.model.*
import com.example.edunova.data.remote.RetrofitClient
import retrofit2.Response

class SessionRepository {
    private val api = RetrofitClient.api

    suspend fun getSessions(token: String): Response<ApiResponse<List<Session>>> {
        return api.getSessions("Bearer $token")
    }

    suspend fun deleteSession(sessionId: Int, token: String): Response<ApiResponse<Any>> {
        return api.deleteSession(sessionId, "Bearer $token")
    }
}