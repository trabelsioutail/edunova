package com.example.edunova.data.model

import com.google.gson.annotations.SerializedName

data class Profile(
    val id: Long,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val age: Int?,
    @SerializedName("profile_image")
    val profileImage: String?,
    val role: String, // "student" ou "teacher"
    val level: String?, // "L1", "L2", "L3", "M1", "M2", "Doctorat"
    @SerializedName("enrollment_year")
    val enrollmentYear: Int?,
    val specialty: String?,
    @SerializedName("years_experience")
    val yearsExperience: Int?,
    @SerializedName("email_verified_at")
    val emailVerifiedAt: String?,
    @SerializedName("remember_token")
    val rememberToken: String?,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String
)