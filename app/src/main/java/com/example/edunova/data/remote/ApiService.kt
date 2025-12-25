package com.example.edunova.data.remote

import com.example.edunova.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ========== AUTHENTIFICATION ==========
    @POST("auth/login.php")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/register.php")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @POST("auth/logout.php")
    suspend fun logout(@Header("Authorization") token: String): Response<ApiResponse<Any>>

    @POST("auth/refresh-token.php")
    suspend fun refreshToken(@Header("Authorization") token: String): Response<AuthResponse>

    // ========== UTILISATEURS ==========
    @GET("users.php")
    suspend fun getUsers(@Header("Authorization") token: String): Response<ApiResponse<List<User>>>

    @GET("users/{id}.php")
    suspend fun getUserById(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<User>>

    @PUT("users/{id}.php")
    suspend fun updateUser(
        @Path("id") userId: Int,
        @Body user: User,
        @Header("Authorization") token: String
    ): Response<ApiResponse<User>>

    @DELETE("users/{id}.php")
    suspend fun deleteUser(
        @Path("id") userId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>

    // ========== PROFILS ==========
    @GET("profile.php")
    suspend fun getProfile(@Header("Authorization") token: String): Response<ApiResponse<Profile>>

    @PUT("profile.php")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Profile>>

    @GET("profiles.php")
    suspend fun getAllProfiles(@Header("Authorization") token: String): Response<ApiResponse<List<Profile>>>

    @GET("profiles/{id}.php")
    suspend fun getProfileById(
        @Path("id") profileId: Long,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Profile>>

    // ========== COURS ==========
    @GET("courses.php")
    suspend fun getCourses(@Header("Authorization") token: String): Response<ApiResponse<List<Course>>>

    @GET("courses/{id}.php")
    suspend fun getCourseById(
        @Path("id") courseId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Course>>

    @POST("courses.php")
    suspend fun createCourse(
        @Body request: CreateCourseRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Course>>

    @PUT("courses/{id}.php")
    suspend fun updateCourse(
        @Path("id") courseId: Int,
        @Body request: CreateCourseRequest,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Course>>

    @DELETE("courses/{id}.php")
    suspend fun deleteCourse(
        @Path("id") courseId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>

    @GET("courses/teacher/{teacherId}.php")
    suspend fun getCoursesByTeacher(
        @Path("teacherId") teacherId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<List<Course>>>

    // ========== SESSIONS ==========
    @GET("sessions.php")
    suspend fun getSessions(@Header("Authorization") token: String): Response<ApiResponse<List<Session>>>

    @DELETE("sessions/{id}.php")
    suspend fun deleteSession(
        @Path("id") sessionId: Int,
        @Header("Authorization") token: String
    ): Response<ApiResponse<Any>>
}