package com.example.edunova.data.model

import com.google.gson.annotations.SerializedName

data class Session(
    val id: Int,
    @SerializedName("user_id")
    val userId: Int,
    val token: String,
    @SerializedName("expires_at")
    val expiresAt: String
)