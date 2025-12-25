package com.example.edunova.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.edunova.data.local.dao.CourseDao
import com.example.edunova.data.mapper.CourseMapper
import com.example.edunova.data.model.*
import com.example.edunova.data.remote.ApiService
import com.example.edunova.data.remote.NetworkResult
import com.example.edunova.data.remote.safeApiCallWithWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository des cours (Critère I.3 - Source de Vérité Unique)
 * Critère II.3 - CRUD Manipulation des Données
 * Gère la logique "faut-il appeler l'API ou lire le cache Room ?"
 */
@Singleton
class CourseRepository @Inject constructor(
    private val apiService: ApiService,
    private val courseDao: CourseDao
) {

    /**
     * Critère I.3 - Source de Vérité Unique
     * Observer les cours depuis Room Database
     */
    fun getAllCourses(): LiveData<List<Course>> {
        return courseDao.getAllCourses().map { entities ->
            CourseMapper.toModelList(entities)
        }
    }

    /**
     * Critère II.3 - CRUD (GET)
     * Récupérer les cours avec stratégie cache-first
     */
    suspend fun fetchCourses(token: String, forceRefresh: Boolean = false): NetworkResult<List<Course>> {
        return withContext(Dispatchers.IO) {
            try {
                // Si pas de refresh forcé, retourner le cache
                if (!forceRefresh) {
                    val cachedCourses = courseDao.getAllCoursesSync()
                    if (cachedCourses.isNotEmpty()) {
                        return@withContext NetworkResult.Success(CourseMapper.toModelList(cachedCourses))
                    }
                }

                // Appeler l'API
                val result = safeApiCallWithWrapper { 
                    apiService.getCourses("Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val courses = result.data
                        // Sauvegarder en cache
                        val courseEntities = CourseMapper.toEntityList(courses)
                        courseDao.deleteAllCourses()
                        courseDao.insertCourses(courseEntities)
                        
                        NetworkResult.Success(courses)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                // En cas d'erreur réseau, retourner le cache
                val cachedCourses = courseDao.getAllCoursesSync()
                if (cachedCourses.isNotEmpty()) {
                    NetworkResult.Success(CourseMapper.toModelList(cachedCourses))
                } else {
                    NetworkResult.Error("Erreur de chargement: ${e.message}")
                }
            }
        }
    }

    /**
     * Critère II.3 - CRUD (GET by ID)
     */
    suspend fun getCourseById(courseId: Int, token: String): NetworkResult<Course> {
        return withContext(Dispatchers.IO) {
            try {
                // Vérifier le cache d'abord
                val cachedCourse = courseDao.getCourseById(courseId)
                if (cachedCourse != null) {
                    return@withContext NetworkResult.Success(CourseMapper.toModel(cachedCourse))
                }

                // Appeler l'API
                val result = safeApiCallWithWrapper { 
                    apiService.getCourseById(courseId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val course = result.data
                        // Sauvegarder en cache
                        val courseEntity = CourseMapper.toEntity(course)
                        courseDao.insertCourse(courseEntity)
                        
                        NetworkResult.Success(course)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de chargement: ${e.message}")
            }
        }
    }

    /**
     * Critère II.3 - CRUD (POST)
     * Créer un nouveau cours
     */
    suspend fun createCourse(
        request: CreateCourseRequest,
        token: String
    ): NetworkResult<Course> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.createCourse(request, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val course = result.data
                        // Ajouter au cache
                        val courseEntity = CourseMapper.toEntity(course)
                        courseDao.insertCourse(courseEntity)
                        
                        NetworkResult.Success(course)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de création: ${e.message}")
            }
        }
    }

    /**
     * Critère II.3 - CRUD (PUT)
     * Mettre à jour un cours
     */
    suspend fun updateCourse(
        courseId: Int,
        request: CreateCourseRequest,
        token: String
    ): NetworkResult<Course> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.updateCourse(courseId, request, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val course = result.data
                        // Mettre à jour le cache
                        val courseEntity = CourseMapper.toEntity(course)
                        courseDao.updateCourse(courseEntity)
                        
                        NetworkResult.Success(course)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de mise à jour: ${e.message}")
            }
        }
    }

    /**
     * Critère II.3 - CRUD (DELETE)
     * Supprimer un cours
     */
    suspend fun deleteCourse(courseId: Int, token: String): NetworkResult<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.deleteCourse(courseId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        // Supprimer du cache
                        courseDao.deleteCourseById(courseId)
                        NetworkResult.Success(true)
                    }
                    is NetworkResult.Error -> NetworkResult.Error(result.message)
                    is NetworkResult.Loading -> NetworkResult.Error("Opération en cours")
                }
            } catch (e: Exception) {
                NetworkResult.Error("Erreur de suppression: ${e.message}")
            }
        }
    }

    /**
     * Observer les cours d'un enseignant
     */
    fun getCoursesByTeacher(teacherId: Int): LiveData<List<Course>> {
        return courseDao.getCoursesByTeacher(teacherId).map { entities ->
            CourseMapper.toModelList(entities)
        }
    }

    /**
     * Récupérer les cours d'un enseignant depuis l'API
     */
    suspend fun fetchCoursesByTeacher(
        teacherId: Int,
        token: String
    ): NetworkResult<List<Course>> {
        return withContext(Dispatchers.IO) {
            try {
                val result = safeApiCallWithWrapper { 
                    apiService.getCoursesByTeacher(teacherId, "Bearer $token") 
                }
                
                when (result) {
                    is NetworkResult.Success -> {
                        val courses = result.data
                        // Mettre à jour le cache pour cet enseignant
                        val courseEntities = CourseMapper.toEntityList(courses)
                        courseEntities.forEach { courseDao.insertCourse(it) }
                        
                        NetworkResult.Success(courses)
                    }
                    is NetworkResult.Error -> result
                    is NetworkResult.Loading -> result
                }
            } catch (e: Exception) {
                // Retourner le cache en cas d'erreur
                val cachedCourses = courseDao.getCoursesByTeacherSync(teacherId)
                if (cachedCourses.isNotEmpty()) {
                    NetworkResult.Success(CourseMapper.toModelList(cachedCourses))
                } else {
                    NetworkResult.Error("Erreur de chargement: ${e.message}")
                }
            }
        }
    }
}