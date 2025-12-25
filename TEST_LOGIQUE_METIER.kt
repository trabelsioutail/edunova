/**
 * 🧪 Tests de Logique Métier EduNova
 * Simulation complète des fonctionnalités dans Kiro
 */

// Simulation des modèles de données
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val isVerified: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class UserEntity(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String,
    val isVerified: Boolean,
    val authToken: String?,
    val isLoggedIn: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class AuthRequest(
    val first_name: String? = null,
    val last_name: String? = null,
    val email: String,
    val password: String
)

data class AuthResponse(
    val success: Boolean,
    val message: String? = null,
    val user: User? = null,
    val token: String? = null
)

// Simulation NetworkResult
sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

// Simulation UserMapper
object UserMapper {
    fun toEntity(user: User, authToken: String? = null, isLoggedIn: Boolean = false): UserEntity {
        return UserEntity(
            id = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
            role = user.role,
            isVerified = user.isVerified,
            authToken = authToken,
            isLoggedIn = isLoggedIn,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
    }

    fun toModel(entity: UserEntity): User {
        return User(
            id = entity.id,
            firstName = entity.firstName,
            lastName = entity.lastName,
            email = entity.email,
            role = entity.role,
            isVerified = entity.isVerified,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }
}

// Simulation DAO
class MockUserDao {
    private val users = mutableListOf<UserEntity>()
    
    fun insertUser(user: UserEntity) {
        users.removeIf { it.id == user.id }
        users.add(user)
        println("💾 [MockDAO] Utilisateur inséré: ${user.email}")
    }
    
    fun getLoggedInUserSync(): UserEntity? {
        return users.find { it.isLoggedIn }
    }
    
    fun logoutAllUsers() {
        users.forEachIndexed { index, user ->
            users[index] = user.copy(isLoggedIn = false, authToken = null)
        }
        println("🚪 [MockDAO] Tous les utilisateurs déconnectés")
    }
    
    fun getAllUsers(): List<UserEntity> = users.toList()
}

// Simulation API Service
class MockApiService {
    private val registeredUsers = mutableListOf<User>()
    
    fun register(request: AuthRequest): NetworkResult<AuthResponse> {
        println("📡 [MockAPI] Tentative d'inscription: ${request.email}")
        
        return if (registeredUsers.any { it.email == request.email }) {
            println("❌ [MockAPI] Email déjà utilisé: ${request.email}")
            NetworkResult.Success(AuthResponse(
                success = false,
                message = "Email déjà utilisé"
            ))
        } else {
            val newUser = User(
                id = registeredUsers.size + 1,
                firstName = request.first_name ?: "",
                lastName = request.last_name ?: "",
                email = request.email,
                role = "etudiant",
                isVerified = false,
                createdAt = "2024-12-25 12:00:00",
                updatedAt = "2024-12-25 12:00:00"
            )
            
            registeredUsers.add(newUser)
            val token = "jwt-${newUser.id}-${System.currentTimeMillis()}"
            
            println("✅ [MockAPI] Inscription réussie: ${newUser.email}")
            NetworkResult.Success(AuthResponse(
                success = true,
                message = "Inscription réussie",
                user = newUser,
                token = token
            ))
        }
    }
    
    fun login(request: AuthRequest): NetworkResult<AuthResponse> {
        println("📡 [MockAPI] Tentative de connexion: ${request.email}")
        
        val user = registeredUsers.find { it.email == request.email }
        
        return if (user != null) {
            val token = "jwt-${user.id}-${System.currentTimeMillis()}"
            println("✅ [MockAPI] Connexion réussie: ${user.email}")
            NetworkResult.Success(AuthResponse(
                success = true,
                message = "Connexion réussie",
                user = user,
                token = token
            ))
        } else {
            println("❌ [MockAPI] Identifiants incorrects: ${request.email}")
            NetworkResult.Success(AuthResponse(
                success = false,
                message = "Identifiants incorrects"
            ))
        }
    }
}

// Simulation AuthRepository
class MockAuthRepository(
    private val apiService: MockApiService,
    private val userDao: MockUserDao
) {
    
    fun register(firstName: String, lastName: String, email: String, password: String): NetworkResult<AuthResponse> {
        println("🔍 [AuthRepository] Début inscription")
        println("📝 [AuthRepository] Données: firstName=$firstName, lastName=$lastName, email=$email")
        
        val result = apiService.register(AuthRequest(
            first_name = firstName,
            last_name = lastName,
            email = email,
            password = password
        ))
        
        println("📊 [AuthRepository] Résultat API: $result")
        
        when (result) {
            is NetworkResult.Success -> {
                val authResponse = result.data
                if (authResponse.success && authResponse.user != null && authResponse.token != null) {
                    println("💾 [AuthRepository] Sauvegarde en cours dans Room Database...")
                    
                    val userEntity = UserMapper.toEntity(
                        user = authResponse.user,
                        authToken = authResponse.token,
                        isLoggedIn = true
                    )
                    
                    println("🔄 [AuthRepository] UserEntity créé: $userEntity")
                    
                    userDao.logoutAllUsers()
                    userDao.insertUser(userEntity)
                    
                    val savedUser = userDao.getLoggedInUserSync()
                    println("🔍 [AuthRepository] Vérification: utilisateur sauvegardé = $savedUser")
                    
                    println("✅ [AuthRepository] Inscription terminée avec succès")
                } else {
                    println("❌ [AuthRepository] Réponse API invalide: success=${authResponse.success}")
                }
            }
            is NetworkResult.Error -> {
                println("❌ [AuthRepository] Erreur API: ${result.message}")
            }
            is NetworkResult.Loading -> {
                println("⏳ [AuthRepository] Chargement...")
            }
        }
        
        return result
    }
    
    fun login(email: String, password: String): NetworkResult<AuthResponse> {
        println("🔍 [AuthRepository] Début connexion")
        println("📝 [AuthRepository] Email: $email")
        
        val result = apiService.login(AuthRequest(email = email, password = password))
        
        when (result) {
            is NetworkResult.Success -> {
                val authResponse = result.data
                if (authResponse.success && authResponse.user != null && authResponse.token != null) {
                    val userEntity = UserMapper.toEntity(
                        user = authResponse.user,
                        authToken = authResponse.token,
                        isLoggedIn = true
                    )
                    
                    userDao.logoutAllUsers()
                    userDao.insertUser(userEntity)
                    
                    println("✅ [AuthRepository] Connexion terminée avec succès")
                }
            }
            is NetworkResult.Error -> {
                println("❌ [AuthRepository] Erreur de connexion: ${result.message}")
            }
            is NetworkResult.Loading -> {
                println("⏳ [AuthRepository] Connexion en cours...")
            }
        }
        
        return result
    }
    
    fun getLoggedInUser(): User? {
        val userEntity = userDao.getLoggedInUserSync()
        return userEntity?.let { UserMapper.toModel(it) }
    }
    
    fun logout() {
        println("🚪 [AuthRepository] Déconnexion")
        userDao.logoutAllUsers()
    }
}

// Tests de validation
object ValidationUtils {
    fun validateRegistration(firstName: String, lastName: String, email: String, password: String): List<String> {
        val errors = mutableListOf<String>()
        
        if (firstName.isBlank()) errors.add("Prénom requis")
        if (lastName.isBlank()) errors.add("Nom requis")
        if (!email.contains("@") || !email.contains(".")) errors.add("Email invalide")
        if (password.length < 6) errors.add("Mot de passe trop court (min 6 caractères)")
        
        return errors
    }
}

// 🧪 TESTS PRINCIPAUX
fun main() {
    println("🚀 === DÉMARRAGE DES TESTS EDUNOVA ===")
    println()
    
    // Initialisation des mocks
    val mockApi = MockApiService()
    val mockDao = MockUserDao()
    val authRepository = MockAuthRepository(mockApi, mockDao)
    
    // Test 1: Validation des données
    println("🧪 TEST 1: Validation des données")
    testValidation()
    println()
    
    // Test 2: Inscription réussie
    println("🧪 TEST 2: Inscription réussie")
    testSuccessfulRegistration(authRepository)
    println()
    
    // Test 3: Email déjà utilisé
    println("🧪 TEST 3: Email déjà utilisé")
    testDuplicateEmail(authRepository)
    println()
    
    // Test 4: Connexion réussie
    println("🧪 TEST 4: Connexion réussie")
    testSuccessfulLogin(authRepository)
    println()
    
    // Test 5: Connexion échouée
    println("🧪 TEST 5: Connexion échouée")
    testFailedLogin(authRepository)
    println()
    
    // Test 6: Gestion de session
    println("🧪 TEST 6: Gestion de session")
    testSessionManagement(authRepository)
    println()
    
    println("🎉 === TOUS LES TESTS TERMINÉS ===")
}

fun testValidation() {
    // Données valides
    val validErrors = ValidationUtils.validateRegistration("John", "Doe", "john@example.com", "password123")
    assert(validErrors.isEmpty()) { "Validation valide échouée: $validErrors" }
    println("✅ Données valides: OK")
    
    // Données invalides
    val invalidErrors = ValidationUtils.validateRegistration("", "", "invalid-email", "123")
    assert(invalidErrors.isNotEmpty()) { "Validation invalide non détectée" }
    println("✅ Données invalides détectées: $invalidErrors")
}

fun testSuccessfulRegistration(authRepository: MockAuthRepository) {
    val result = authRepository.register("John", "Doe", "john@example.com", "password123")
    
    when (result) {
        is NetworkResult.Success -> {
            assert(result.data.success) { "Inscription échouée: ${result.data.message}" }
            println("✅ Inscription réussie")
            
            // Vérifier que l'utilisateur est connecté
            val loggedUser = authRepository.getLoggedInUser()
            assert(loggedUser != null) { "Utilisateur non connecté après inscription" }
            assert(loggedUser?.email == "john@example.com") { "Email incorrect: ${loggedUser?.email}" }
            println("✅ Utilisateur connecté: ${loggedUser?.email}")
        }
        is NetworkResult.Error -> {
            throw AssertionError("Inscription échouée: ${result.message}")
        }
        is NetworkResult.Loading -> {
            throw AssertionError("État de chargement inattendu")
        }
    }
}

fun testDuplicateEmail(authRepository: MockAuthRepository) {
    // Tentative d'inscription avec le même email
    val result = authRepository.register("Jane", "Smith", "john@example.com", "password456")
    
    when (result) {
        is NetworkResult.Success -> {
            assert(!result.data.success) { "Duplication d'email non détectée" }
            assert(result.data.message == "Email déjà utilisé") { "Message incorrect: ${result.data.message}" }
            println("✅ Duplication d'email détectée")
        }
        is NetworkResult.Error -> {
            throw AssertionError("Erreur inattendue: ${result.message}")
        }
        is NetworkResult.Loading -> {
            throw AssertionError("État de chargement inattendu")
        }
    }
}

fun testSuccessfulLogin(authRepository: MockAuthRepository) {
    val result = authRepository.login("john@example.com", "password123")
    
    when (result) {
        is NetworkResult.Success -> {
            assert(result.data.success) { "Connexion échouée: ${result.data.message}" }
            println("✅ Connexion réussie")
            
            val loggedUser = authRepository.getLoggedInUser()
            assert(loggedUser != null) { "Utilisateur non connecté après login" }
            println("✅ Utilisateur connecté: ${loggedUser?.email}")
        }
        is NetworkResult.Error -> {
            throw AssertionError("Connexion échouée: ${result.message}")
        }
        is NetworkResult.Loading -> {
            throw AssertionError("État de chargement inattendu")
        }
    }
}

fun testFailedLogin(authRepository: MockAuthRepository) {
    val result = authRepository.login("nonexistent@example.com", "wrongpassword")
    
    when (result) {
        is NetworkResult.Success -> {
            assert(!result.data.success) { "Connexion invalide non détectée" }
            assert(result.data.message == "Identifiants incorrects") { "Message incorrect: ${result.data.message}" }
            println("✅ Connexion invalide détectée")
        }
        is NetworkResult.Error -> {
            throw AssertionError("Erreur inattendue: ${result.message}")
        }
        is NetworkResult.Loading -> {
            throw AssertionError("État de chargement inattendu")
        }
    }
}

fun testSessionManagement(authRepository: MockAuthRepository) {
    // Vérifier qu'un utilisateur est connecté
    val userBeforeLogout = authRepository.getLoggedInUser()
    assert(userBeforeLogout != null) { "Aucun utilisateur connecté avant déconnexion" }
    println("✅ Utilisateur connecté avant déconnexion: ${userBeforeLogout?.email}")
    
    // Déconnecter
    authRepository.logout()
    
    // Vérifier qu'aucun utilisateur n'est connecté
    val userAfterLogout = authRepository.getLoggedInUser()
    assert(userAfterLogout == null) { "Utilisateur encore connecté après déconnexion: ${userAfterLogout?.email}" }
    println("✅ Déconnexion réussie")
}

// Exécuter les tests
main()