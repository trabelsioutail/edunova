package com.example.edunova.data.model

import com.google.gson.annotations.SerializedName

data class Course(
    val id: Int,
    val title: String,
    val description: String?,
    @SerializedName("teacherId")
    val teacherId: Int,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String
)