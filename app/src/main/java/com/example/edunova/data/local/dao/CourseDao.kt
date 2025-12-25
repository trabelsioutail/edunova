package com.example.edunova.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.edunova.data.local.entity.CourseEntity

/**
 * DAO pour les opérations Course (Critère I.4 - Couche Data)
 * CRUD complet pour la gestion des cours
 */
@Dao
interface CourseDao {

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    fun getAllCourses(): LiveData<List<CourseEntity>>

    @Query("SELECT * FROM courses ORDER BY createdAt DESC")
    suspend fun getAllCoursesSync(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :courseId")
    suspend fun getCourseById(courseId: Int): CourseEntity?

    @Query("SELECT * FROM courses WHERE teacherId = :teacherId")
    fun getCoursesByTeacher(teacherId: Int): LiveData<List<CourseEntity>>

    @Query("SELECT * FROM courses WHERE teacherId = :teacherId")
    suspend fun getCoursesByTeacherSync(teacherId: Int): List<CourseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: CourseEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourses(courses: List<CourseEntity>)

    @Update
    suspend fun updateCourse(course: CourseEntity)

    @Delete
    suspend fun deleteCourse(course: CourseEntity)

    @Query("DELETE FROM courses WHERE id = :courseId")
    suspend fun deleteCourseById(courseId: Int)

    @Query("DELETE FROM courses")
    suspend fun deleteAllCourses()

    @Query("UPDATE courses SET isSynced = :synced WHERE id = :courseId")
    suspend fun updateSyncStatus(courseId: Int, synced: Boolean)
}