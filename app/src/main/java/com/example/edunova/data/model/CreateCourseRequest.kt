package com.example.edunova.data.model

data class CreateCourseRequest(
    val title: String,
    val description: String?,
    val teacherId: Int
)