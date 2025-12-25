# Guide d'Intégration de la Base de Données EduNova

## 📋 Vue d'ensemble

Ce guide explique comment votre base de données MySQL `edunova` a été intégrée dans votre application Android. L'architecture suit les meilleures pratiques Android avec une séparation claire des couches.

## 🏗️ Architecture

```
app/
├── data/
│   ├── local/
│   │   └── PreferencesManager.kt          # Gestion du stockage local
│   ├── model/
│   │   ├── User.kt                        # Modèle utilisateur
│   │   ├── Profile.kt                     # Modèle profil
│   │   ├── Course.kt                      # Modèle cours
│   │   ├── Session.kt                     # Modèle session
│   │   ├── AuthRequest.kt                 # Requête d'authentification
│   │   ├── AuthResponse.kt                # Réponse d'authentification
│   │   ├── CreateCourseRequest.kt         # Requête création cours
│   │   ├── UpdateProfileRequest.kt        # Requête mise à jour profil
│   │   └── ApiResponse.kt                 # Réponse API générique
│   ├── remote/
│   │   ├── ApiService.kt                  # Interface API Retrofit
│   │   ├── RetrofitClient.kt              # Configuration Retrofit
│   │   ├── NetworkResult.kt               # Gestion des résultats réseau
│   │   └── SafeApiCall.kt                 # Appels API sécurisés
│   └── repository/
│       ├── AuthRepository.kt              # Repository authentification
│       ├── UserRepository.kt              # Repository utilisateurs
│       ├── ProfileRepository.kt           # Repository profils
│       ├── CourseRepository.kt            # Repository cours
│       └── SessionRepository.kt           # Repository sessions
├── ui/
│   └── viewmodel/
│       ├── AuthViewModel.kt               # ViewModel authentification
│       ├── CourseViewModel.kt             # ViewModel cours
│       └── ProfileViewModel.kt            # ViewModel profils
└── utils/
    └── Constants.kt                       # Constantes de l'application
```

## 🗄️ Modèles de Données

### User (Table `users`)
```kotlin
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: String, // "admin", "enseignant", "etudiant"
    val isVerified: Boolean,
    // ... autres champs
)
```

### Profile (Table `profile`)
```kotlin
data class Profile(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String?,
    val address: String?,
    val age: Int?,
    val role: String, // "student", "teacher"
    val level: String?, // "L1", "L2", "L3", "M1", "M2", "Doctorat"
    // ... autres champs
)
```

### Course (Table `courses`)
```kotlin
data class Course(
    val id: Int,
    val title: String,
    val description: String?,
    val teacherId: Int,
    val createdAt: String,
    val updatedAt: String
)
```

## 🔧 Configuration

### 1. URL de l'API
Modifiez l'URL dans `Constants.kt` selon votre environnement :

```kotlin
object Constants {
    // Pour émulateur Android
    const val BASE_URL = "http://10.0.2.2:8080/"
    
    // Pour téléphone réel (remplacez XX par votre IP)
    // const val BASE_URL = "http://192.168.1.XX/edunova_api/"
    
    // Pour production
    // const val BASE_URL = "https://votre-domaine.com/api/"
}
```

### 2. Endpoints API
L'interface `ApiService` définit tous les endpoints nécessaires :

- **Authentification** : `/auth/login.php`, `/auth/register.php`, `/auth/logout.php`
- **Utilisateurs** : `/users.php`, `/users/{id}.php`
- **Profils** : `/profile.php`, `/profiles.php`, `/profiles/{id}.php`
- **Cours** : `/courses.php`, `/courses/{id}.php`, `/courses/teacher/{teacherId}.php`
- **Sessions** : `/sessions.php`, `/sessions/{id}.php`

## 💻 Utilisation dans les ViewModels

### Authentification
```kotlin
class AuthViewModel(private val context: Context) : ViewModel() {
    private val authRepository = AuthRepository()
    private val preferencesManager = PreferencesManager(context)
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = safeApiCall { 
                authRepository.login(email, password) 
            }
            // Traitement du résultat...
        }
    }
}
```

### Gestion des Cours
```kotlin
class CourseViewModel(private val context: Context) : ViewModel() {
    private val courseRepository = CourseRepository()
    
    fun getCourses() {
        viewModelScope.launch {
            val token = preferencesManager.getAuthToken().first()
            val result = safeApiCall { 
                courseRepository.getCourses(token!!) 
            }
            // Traitement du résultat...
        }
    }
}
```

## 🔐 Gestion de l'Authentification

### Stockage Local
Le `PreferencesManager` utilise DataStore pour sauvegarder :
- Token d'authentification
- Informations utilisateur (ID, email, rôle, nom)
- État de connexion

### Utilisation
```kotlin
// Sauvegarder les données utilisateur
preferencesManager.saveAuthToken(token)
preferencesManager.saveUserInfo(userId, email, role, name)

// Récupérer les données
val token = preferencesManager.getAuthToken().first()
val isLoggedIn = preferencesManager.isLoggedIn().first()

// Déconnexion
preferencesManager.clearUserData()
```

## 🌐 Gestion des Erreurs Réseau

### NetworkResult
```kotlin
sealed class NetworkResult<T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error<T>(val message: String) : NetworkResult<T>()
    data class Loading<T>(val isLoading: Boolean = true) : NetworkResult<T>()
}
```

### SafeApiCall
```kotlin
suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            NetworkResult.Success(response.body()!!)
        } else {
            NetworkResult.Error("Erreur ${response.code()}")
        }
    } catch (e: Exception) {
        NetworkResult.Error(e.message ?: "Erreur inconnue")
    }
}
```

## 📱 Utilisation dans les Composables

```kotlin
@Composable
fun LoginScreen() {
    val authViewModel = remember { AuthViewModel(LocalContext.current) }
    val loginState by authViewModel.loginState.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val errorMessage by authViewModel.errorMessage.collectAsState()
    
    // Interface utilisateur...
    
    Button(
        onClick = { authViewModel.login(email, password) },
        enabled = !isLoading
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Text("Se connecter")
        }
    }
    
    // Gestion des états
    LaunchedEffect(loginState) {
        when (loginState) {
            is NetworkResult.Success -> {
                // Redirection vers l'écran principal
            }
            is NetworkResult.Error -> {
                // Afficher l'erreur
            }
        }
    }
}
```

## 🔄 Synchronisation avec la Base de Données

### Structure de la Base de Données
Votre base de données MySQL contient 4 tables principales :
1. **users** - Informations d'authentification
2. **profile** - Profils détaillés des utilisateurs
3. **courses** - Cours disponibles
4. **sessions** - Sessions d'authentification

### Correspondance Modèles-Tables
- `User.kt` ↔ Table `users`
- `Profile.kt` ↔ Table `profile`
- `Course.kt` ↔ Table `courses`
- `Session.kt` ↔ Table `sessions`

## 🚀 Prochaines Étapes

1. **Créer l'API Backend** : Développez les endpoints PHP correspondants
2. **Tester les Endpoints** : Utilisez Postman ou un outil similaire
3. **Implémenter l'Interface** : Créez les écrans Compose correspondants
4. **Ajouter la Validation** : Validez les données côté client
5. **Gérer les Permissions** : Implémentez la logique de rôles
6. **Optimiser les Performances** : Ajoutez la mise en cache si nécessaire

## 📝 Notes Importantes

- Tous les modèles utilisent `@SerializedName` pour la compatibilité JSON
- Les appels API sont sécurisés avec `safeApiCall()`
- Le stockage local utilise DataStore (plus moderne que SharedPreferences)
- L'architecture respecte les principes MVVM
- La gestion d'état utilise StateFlow pour la réactivité

## 🔧 Dépendances Ajoutées

Les dépendances suivantes sont déjà configurées dans votre `build.gradle.kts` :
- Retrofit + Gson pour les appels API
- DataStore pour le stockage local
- Coroutines pour la programmation asynchrone
- ViewModel + Compose pour l'architecture MVVM

Votre intégration de base de données est maintenant prête ! 🎉