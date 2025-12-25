# 🔍 Diagnostic - Problème d'Inscription Base de Données

## 🎯 **Problème Identifié**

L'inscription ne sauvegarde pas les données dans la base Room Database locale.

## 🔧 **Causes Possibles**

### 1. **Problème de Réponse API**
- L'API ne retourne pas les bonnes données
- Le format de réponse ne correspond pas au modèle `AuthResponse`
- L'API retourne `success: false`

### 2. **Problème de Mapping**
- Erreur dans `UserMapper.toEntity()`
- Champs manquants ou mal mappés
- Types incompatibles

### 3. **Problème de Base de Données Room**
- Base de données non initialisée
- Erreur dans les DAOs
- Contraintes de clés primaires

### 4. **Problème de Configuration Hilt**
- Injection de dépendances incorrecte
- Modules mal configurés

## 🧪 **Tests Ajoutés pour Diagnostic**

### **1. Logs Détaillés dans AuthRepository**
```kotlin
println("🔍 AuthRepository.register() - Début inscription")
println("📝 Données: firstName=$firstName, lastName=$lastName, email=$email")
println("📡 Réponse API: $result")
println("✅ Réponse API Success: success=${authResponse.success}")
println("💾 Sauvegarde en cours dans Room Database...")
println("🔄 UserEntity créé: $userEntity")
println("✅ Utilisateur sauvegardé dans Room Database")
```

### **2. Logs Détaillés dans AuthViewModel**
```kotlin
println("🎯 AuthViewModel.register() - Début")
println("📝 Données: firstName=$firstName, lastName=$lastName, email=$email")
println("📊 Résultat AuthRepository: $result")
println("✅ Inscription réussie dans ViewModel")
```

### **3. Fonction de Test de Base de Données**
```kotlin
suspend fun testDatabaseConnection(): NetworkResult<Boolean> {
    // Teste l'insertion, récupération et suppression
    // Utilisateur fictif pour vérifier Room Database
}
```

### **4. Bouton de Test dans RegisterActivity**
- Bouton "🧪 Tester la base de données"
- Permet de vérifier si Room fonctionne

## 🔍 **Comment Déboguer**

### **Étape 1 : Tester la Base de Données**
1. Ouvrir l'app
2. Aller sur l'écran d'inscription
3. Cliquer sur "🧪 Tester la base de données"
4. Vérifier les logs dans Logcat

**Résultat attendu :**
```
🧪 Test de connexion à la base de données...
✅ Utilisateur de test inséré
🔍 Utilisateur récupéré: UserEntity(...)
✅ Base de données fonctionne correctement
🧹 Utilisateur de test supprimé
```

### **Étape 2 : Tester l'Inscription**
1. Remplir le formulaire d'inscription
2. Cliquer sur "S'inscrire"
3. Vérifier les logs dans Logcat

**Logs à surveiller :**
```
🎯 AuthViewModel.register() - Début
📝 Données: firstName=John, lastName=Doe, email=john@example.com
🔍 AuthRepository.register() - Début inscription
📡 Réponse API: NetworkResult.Success(...)
✅ Réponse API Success: success=true, user=User(...), token=abc123
💾 Sauvegarde en cours dans Room Database...
🔄 UserEntity créé: UserEntity(...)
✅ Utilisateur sauvegardé dans Room Database
🔍 Vérification: utilisateur sauvegardé = UserEntity(...)
```

## 🚨 **Problèmes Potentiels et Solutions**

### **Problème 1 : API ne répond pas**
**Symptôme :** `NetworkResult.Error("Erreur de connexion réseau")`

**Solutions :**
- Vérifier l'URL de l'API dans `NetworkModule`
- Vérifier la connexion internet
- Tester l'API avec Postman

### **Problème 2 : API retourne success=false**
**Symptôme :** `success=false` dans les logs

**Solutions :**
- Vérifier les données envoyées à l'API
- Vérifier que l'email n'existe pas déjà
- Vérifier les validations côté serveur

### **Problème 3 : Erreur de mapping**
**Symptôme :** Exception dans `UserMapper.toEntity()`

**Solutions :**
- Vérifier que tous les champs requis sont présents
- Vérifier les types de données
- Ajouter des valeurs par défaut

### **Problème 4 : Base de données Room**
**Symptôme :** Exception lors de `userDao.insertUser()`

**Solutions :**
- Vérifier que la base est initialisée
- Vérifier les contraintes de clés primaires
- Nettoyer et reconstruire l'app

## 🔧 **Actions Correctives**

### **Si l'API ne fonctionne pas :**
```kotlin
// Mode hors ligne temporaire
if (result is NetworkResult.Error) {
    // Créer un utilisateur fictif pour tester
    val fakeUser = User(
        id = System.currentTimeMillis().toInt(),
        firstName = firstName,
        lastName = lastName,
        email = email,
        role = "etudiant",
        isVerified = false,
        createdAt = "2024-01-01 00:00:00",
        updatedAt = "2024-01-01 00:00:00"
    )
    
    val userEntity = UserMapper.toEntity(
        user = fakeUser,
        authToken = "fake-token-${System.currentTimeMillis()}",
        isLoggedIn = true
    )
    
    userDao.logoutAllUsers()
    userDao.insertUser(userEntity)
    
    return NetworkResult.Success(AuthResponse(
        success = true,
        message = "Inscription hors ligne réussie",
        user = fakeUser,
        token = userEntity.authToken
    ))
}
```

### **Si la base de données ne fonctionne pas :**
```kotlin
// Vérifier la configuration Room dans DatabaseModule
@Provides
@Singleton
fun provideDatabase(@ApplicationContext context: Context): EduNovaDatabase {
    return Room.databaseBuilder(
        context,
        EduNovaDatabase::class.java,
        "edunova_database"
    )
    .fallbackToDestructiveMigration() // Ajouter cette ligne
    .build()
}
```

## 📊 **Checklist de Vérification**

- [ ] **Test de base de données** : Bouton de test fonctionne
- [ ] **Logs d'inscription** : Tous les logs apparaissent
- [ ] **Réponse API** : `success=true` dans les logs
- [ ] **Mapping** : `UserEntity` créé correctement
- [ ] **Sauvegarde** : Utilisateur présent dans Room
- [ ] **Navigation** : Redirection vers MainActivity après inscription

## 🎯 **Prochaines Étapes**

1. **Exécuter l'app** avec les nouveaux logs
2. **Tester la base de données** avec le bouton de test
3. **Tenter une inscription** et analyser les logs
4. **Identifier le point de défaillance** exact
5. **Appliquer la solution** correspondante

---

## 📱 **Instructions d'Utilisation**

1. **Compiler l'app** : `./gradlew assembleDebug`
2. **Installer sur l'appareil**
3. **Ouvrir Logcat** pour voir les logs
4. **Tester l'inscription** avec les nouveaux diagnostics
5. **Analyser les résultats** selon ce guide

Les logs détaillés vous permettront d'identifier précisément où le problème se situe dans le flux d'inscription.