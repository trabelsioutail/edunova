# 🚀 Intégration Base de Données EduNova - Guide de Démarrage Rapide

## ✅ Ce qui a été fait

Votre base de données MySQL `edunova` a été complètement intégrée dans votre application Android avec :

### 📁 Structure créée
- **4 modèles de données** correspondant à vos tables MySQL
- **5 repositories** pour gérer les opérations CRUD
- **3 ViewModels** pour la logique métier
- **Gestion complète de l'authentification** avec stockage sécurisé
- **Gestion d'erreurs réseau** robuste
- **Architecture MVVM** respectant les bonnes pratiques Android

### 🔧 Fichiers créés/modifiés

#### Modèles de données
- `User.kt` - Correspond à la table `users`
- `Profile.kt` - Correspond à la table `profile`  
- `Course.kt` - Correspond à la table `courses`
- `Session.kt` - Correspond à la table `sessions`
- `ApiResponse.kt`, `AuthRequest.kt`, `AuthResponse.kt` - Modèles utilitaires

#### Couche réseau
- `ApiService.kt` - Interface Retrofit avec tous les endpoints
- `RetrofitClient.kt` - Configuration Retrofit mise à jour
- `NetworkResult.kt` - Gestion des états réseau
- `SafeApiCall.kt` - Appels API sécurisés

#### Repositories
- `AuthRepository.kt` - Authentification
- `UserRepository.kt` - Gestion des utilisateurs
- `ProfileRepository.kt` - Gestion des profils
- `CourseRepository.kt` - Gestion des cours
- `SessionRepository.kt` - Gestion des sessions

#### ViewModels
- `AuthViewModel.kt` - Logique d'authentification (mis à jour)
- `CourseViewModel.kt` - Logique des cours
- `ProfileViewModel.kt` - Logique des profils

#### Utilitaires
- `PreferencesManager.kt` - Stockage local sécurisé
- `Constants.kt` - Constantes de l'application
- `ExampleUsage.kt` - Exemples d'utilisation

## 🎯 Prochaines étapes

### 1. Configurer votre serveur backend

Vous devez créer les endpoints PHP correspondants. Voici la structure recommandée :

```
votre-serveur/
├── auth/
│   ├── login.php
│   ├── register.php
│   └── logout.php
├── users.php
├── profile.php
├── courses.php
└── sessions.php
```

### 2. Modifier l'URL de base

Dans `Constants.kt`, changez l'URL selon votre configuration :

```kotlin
// Pour émulateur Android
const val BASE_URL = "http://10.0.2.2:8080/"

// Pour téléphone réel (remplacez par votre IP)
const val BASE_URL = "http://192.168.1.100/edunova_api/"

// Pour production
const val BASE_URL = "https://votre-domaine.com/api/"
```

### 3. Tester l'intégration

Utilisez les exemples dans `ExampleUsage.kt` pour tester :

```kotlin
// Dans votre Activity ou Composable
@Composable
fun MyScreen() {
    val context = LocalContext.current
    val authViewModel = remember { AuthViewModel(context) }
    
    // Utiliser le ViewModel...
    authViewModel.login("test@example.com", "password")
}
```

## 📋 Endpoints API requis

Votre backend doit implémenter ces endpoints :

### Authentification
- `POST /auth/login.php` - Connexion
- `POST /auth/register.php` - Inscription  
- `POST /auth/logout.php` - Déconnexion

### Utilisateurs
- `GET /users.php` - Liste des utilisateurs
- `GET /users/{id}.php` - Utilisateur par ID
- `PUT /users/{id}.php` - Modifier utilisateur
- `DELETE /users/{id}.php` - Supprimer utilisateur

### Profils
- `GET /profile.php` - Profil de l'utilisateur connecté
- `PUT /profile.php` - Modifier le profil
- `GET /profiles.php` - Tous les profils (admin)
- `GET /profiles/{id}.php` - Profil par ID

### Cours
- `GET /courses.php` - Liste des cours
- `POST /courses.php` - Créer un cours
- `GET /courses/{id}.php` - Cours par ID
- `PUT /courses/{id}.php` - Modifier un cours
- `DELETE /courses/{id}.php` - Supprimer un cours
- `GET /courses/teacher/{teacherId}.php` - Cours d'un enseignant

### Sessions
- `GET /sessions.php` - Sessions actives
- `DELETE /sessions/{id}.php` - Supprimer une session

## 🔐 Format des réponses API

Toutes les réponses doivent suivre ce format :

```json
{
    "success": true,
    "message": "Opération réussie",
    "data": { /* vos données */ },
    "error": null
}
```

### Exemple pour login.php :
```json
{
    "success": true,
    "message": "Connexion réussie",
    "user": {
        "id": 1,
        "first_name": "John",
        "last_name": "Doe",
        "email": "john@example.com",
        "role": "etudiant"
    },
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 🧪 Comment tester

### 1. Tester l'authentification
```kotlin
val authViewModel = AuthViewModel(context)
authViewModel.login("test@example.com", "password")

// Observer le résultat
authViewModel.loginState.collect { state ->
    when (state) {
        is NetworkResult.Success -> println("Connexion réussie!")
        is NetworkResult.Error -> println("Erreur: ${state.message}")
        is NetworkResult.Loading -> println("Connexion en cours...")
    }
}
```

### 2. Tester les cours
```kotlin
val courseViewModel = CourseViewModel(context)
courseViewModel.getCourses()

// Observer les cours
courseViewModel.coursesState.collect { state ->
    when (state) {
        is NetworkResult.Success -> {
            val courses = state.data
            println("${courses.size} cours trouvés")
        }
        is NetworkResult.Error -> println("Erreur: ${state.message}")
    }
}
```

## 🔧 Dépendances requises

Ces dépendances sont déjà configurées dans votre `build.gradle.kts` :

```kotlin
// Retrofit pour les appels API
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.14")

// DataStore pour le stockage local
implementation("androidx.datastore:datastore-preferences:1.1.1")

// Coroutines pour l'asynchrone
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

// ViewModel et Compose
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
```

## 🚨 Points importants

1. **Sécurité** : Tous les appels API utilisent des tokens d'authentification
2. **Gestion d'erreurs** : Chaque appel est sécurisé avec `safeApiCall()`
3. **Stockage local** : Les données utilisateur sont stockées de manière sécurisée
4. **Architecture** : Respecte les principes MVVM et Clean Architecture
5. **Réactivité** : Utilise StateFlow pour la réactivité des données

## 📞 Support

Si vous rencontrez des problèmes :

1. Vérifiez que votre serveur backend est accessible
2. Consultez les logs Logcat pour les erreurs réseau
3. Testez vos endpoints avec Postman
4. Vérifiez la configuration de l'URL dans `Constants.kt`

## 🎉 Félicitations !

Votre base de données MySQL est maintenant parfaitement intégrée dans votre application Android. Vous pouvez commencer à développer vos écrans en utilisant les ViewModels fournis !

---

**Prochaine étape recommandée** : Créez vos endpoints PHP backend et testez la connexion avec l'application.