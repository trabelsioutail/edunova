package com.example.edunova.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val email: String,
    @SerializedName("password_hash")
    val passwordHash: String? = null,
    val role: String, // "admin", "enseignant", "etudiant"
    @SerializedName("google_id")
    val googleId: String? = null,
    @SerializedName("is_active")
    val isActive: Boolean = true,
    @SerializedName("created_at")
    val createdAt: String,
    @SerializedName("updated_at")
    val updatedAt: String,
    @SerializedName("reset_password_token")
    val resetPasswordToken: String? = null,
    @SerializedName("reset_password_expires")
    val resetPasswordExpires: String? = null,
    @SerializedName("last_login")
    val lastLogin: String? = null,
    @SerializedName("verification_token")
    val verificationToken: String? = null,
    @SerializedName("verification_token_expires")
    val verificationTokenExpires: String? = null,
    @SerializedName("is_verified")
    val isVerified: Boolean = false
)