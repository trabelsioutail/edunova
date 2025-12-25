package com.example.edunova.data.model

import com.google.gson.annotations.SerializedName

data class UpdateProfileRequest(
    @SerializedName("first_name")
    val firstName: String?,
    @SerializedName("last_name")
    val lastName: String?,
    val phone: String?,
    val address: String?,
    val age: Int?,
    @SerializedName("profile_image")
    val profileImage: String?,
    val level: String?,
    @SerializedName("enrollment_year")
    val enrollmentYear: Int?,
    val specialty: String?,
    @SerializedName("years_experience")
    val yearsExperience: Int?
)