package com.example.edunova.data.mapper

import com.example.edunova.data.local.entity.CourseEntity
import com.example.edunova.data.model.Course

/**
 * Mapper pour convertir entre Course (API) et CourseEntity (Room)
 * Critère I.4 - Couche Data : Abstraction des sources de données
 */
object CourseMapper {

    fun toEntity(course: Course, isSynced: Boolean = true): CourseEntity {
        return CourseEntity(
            id = course.id,
            title = course.title,
            description = course.description,
            teacherId = course.teacherId,
            createdAt = course.createdAt,
            updatedAt = course.updatedAt,
            isSynced = isSynced
        )
    }

    fun toModel(entity: CourseEntity): Course {
        return Course(
            id = entity.id,
            title = entity.title,
            description = entity.description,
            teacherId = entity.teacherId,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntityList(courses: List<Course>): List<CourseEntity> {
        return courses.map { toEntity(it) }
    }

    fun toModelList(entities: List<CourseEntity>): List<Course> {
        return entities.map { toModel(it) }
    }
}