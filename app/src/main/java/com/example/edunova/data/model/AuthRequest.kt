package com.example.edunova.data.model

data class AuthRequest(
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String,
    val password: String
)