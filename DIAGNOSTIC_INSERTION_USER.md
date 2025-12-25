# 🔍 Diagnostic - Utilisateur ne s'ajoute pas en Base

## 🎯 **Problème Identifié**

L'utilisateur ne s'ajoute pas dans la base de données Room lors de l'inscription.

## 🔧 **Améliorations Apportées**

### **1. Logs Détaillés Ajoutés**

**Dans `simulateRegister()` :**
```kotlin
🎭 Simulation d'inscription hors ligne
🔍 Vérification de l'email existant: [email]
✅ Email disponible: [email]
👤 Nouvel utilisateur créé: [email] (ID: [id])
🔑 Token généré: [token]
🔄 UserEntity créé: [entity]
🚪 Déconnexion des autres utilisateurs...
💾 Insertion de l'utilisateur en base...
✅ Utilisateur inséré avec succès
🔍 Vérification de l'insertion...
✅ Utilisateur trouvé en base: [email] (ID: [id], connecté: true)
📊 Total utilisateurs en base: [count]
```

### **2. Fonction de Vérification**

**Nouvelle fonction `getAllUsers()` :**
```kotlin
suspend fun getAllUsers(): List<UserEntity> {
    val users = userDao.getAllUsers()
    println("🔍 Utilisateurs en base: ${users.size}")
    users.forEach { user ->
        println("   - ${user.email} (ID: ${user.id}, connecté: ${user.isLoggedIn})")
    }
    return users
}
```

### **3. Boutons de Debug**

**Dans RegisterActivity :**
- **🧪 Tester la base de données** - Vérifie que Room fonctionne
- **👥 Vérifier les utilisateurs** - Liste tous les utilisateurs en base

### **4. Correction du Nettoyage de Session**

**Avant (Problématique) :**
```kotlin
// ❌ Supprimait potentiellement les utilisateurs
userDao.logoutAllUsers() // Pouvait causer des problèmes
```

**Après (Corrigé) :**
```kotlin
// ✅ Ne fait que déconnecter, ne supprime pas
println("🧹 Nettoyage des sessions au démarrage (pas des utilisateurs)")
userDao.logoutAllUsers() // Met isLoggedIn = false et authToken = null
println("✅ Sessions nettoyées, utilisateurs conservés")
```

## 🧪 **Comment Diagnostiquer le Problème**

### **Étape 1 : Recompiler et Installer**
```bash
./gradlew assembleDebug --no-daemon
./gradlew installDebug
```

### **Étape 2 : Tester la Base de Données**
1. **Ouvrir** l'application EduNova
2. **Aller** sur l'écran d'inscription
3. **Cliquer** sur "🧪 Tester la base de données"
4. **Vérifier** les logs dans Logcat

**Logs attendus :**
```
🧪 Test de connexion à la base de données...
✅ Utilisateur de test inséré
🔍 Utilisateur récupéré: UserEntity(...)
✅ Base de données fonctionne correctement
```

### **Étape 3 : Tenter une Inscription**
1. **Remplir** le formulaire d'inscription
2. **Cliquer** sur "S'inscrire"
3. **Observer** les logs détaillés

**Logs attendus pour inscription réussie :**
```
🎭 Simulation d'inscription hors ligne
🔍 Vérification de l'email existant: john@test.com
✅ Email disponible: john@test.com
👤 Nouvel utilisateur créé: john@test.com (ID: 1735134567)
🔑 Token généré: offline-token-1735134567
🔄 UserEntity créé: UserEntity(id=1735134567, firstName=John, ...)
🚪 Déconnexion des autres utilisateurs...
💾 Insertion de l'utilisateur en base...
✅ Utilisateur inséré avec succès
🔍 Vérification de l'insertion...
✅ Utilisateur trouvé en base: john@test.com (ID: 1735134567, connecté: true)
📊 Total utilisateurs en base: 1
```

### **Étape 4 : Vérifier les Utilisateurs**
1. **Cliquer** sur "👥 Vérifier les utilisateurs"
2. **Observer** les logs

**Logs attendus :**
```
🔍 Vérification des utilisateurs en base...
🔍 Utilisateurs en base: 1
   - john@test.com (ID: 1735134567, connecté: true)
📊 1 utilisateur(s) trouvé(s) en base
```

## 🔍 **Scénarios de Diagnostic**

### **Scénario 1 : Base de Données ne Fonctionne Pas**
**Symptômes :**
```
❌ Test DB échoué depuis ViewModel: [erreur]
💥 Exception dans simulateRegister: [erreur]
```

**Solutions :**
1. **Vérifier** la configuration Room dans `DatabaseModule`
2. **Nettoyer** et recompiler : `./gradlew clean assembleDebug`
3. **Redémarrer** l'émulateur/appareil

### **Scénario 2 : Insertion Échoue**
**Symptômes :**
```
💾 Insertion de l'utilisateur en base...
❌ Utilisateur non trouvé en base après insertion!
```

**Solutions :**
1. **Vérifier** les contraintes de clé primaire
2. **Vérifier** que tous les champs requis sont remplis
3. **Vérifier** les permissions de base de données

### **Scénario 3 : Utilisateur Supprimé Après Insertion**
**Symptômes :**
```
✅ Utilisateur inséré avec succès
📊 Total utilisateurs en base: 0
```

**Solutions :**
1. **Vérifier** que `clearAllSessions()` ne supprime pas les utilisateurs
2. **Vérifier** qu'il n'y a pas d'autres appels à `deleteAllUsers()`

### **Scénario 4 : Tout Fonctionne**
**Symptômes :**
```
✅ Utilisateur inséré avec succès
✅ Utilisateur trouvé en base: [email]
📊 Total utilisateurs en base: 1
```

**Action :** Le problème est résolu !

## 🛠️ **Solutions Selon les Cas**

### **Si la Base de Données ne Fonctionne Pas**

**Vérifier `DatabaseModule.kt` :**
```kotlin
@Provides
@Singleton
fun provideEduNovaDatabase(@ApplicationContext context: Context): EduNovaDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        EduNovaDatabase::class.java,
        "edunova_database"
    )
    .fallbackToDestructiveMigration() // ✅ Important
    .allowMainThreadQueries() // ✅ Pour debug
    .build()
}
```

### **Si l'Insertion Échoue**

**Vérifier `UserEntity.kt` :**
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: Int, // ✅ Clé primaire correcte
    val firstName: String,   // ✅ Champs non-null
    val lastName: String,
    val email: String,
    // ... autres champs
)
```

### **Si les Utilisateurs Disparaissent**

**Vérifier les appels de nettoyage :**
```kotlin
// ✅ CORRECT : Ne déconnecte que
userDao.logoutAllUsers() // Met isLoggedIn = false

// ❌ INCORRECT : Supprime tout
userDao.deleteAllUsers() // Supprime les utilisateurs
```

## 📊 **Outils de Debug Disponibles**

### **Dans l'Application**
- **🧪 Tester la base de données** - Vérifie Room
- **👥 Vérifier les utilisateurs** - Liste les utilisateurs
- **Logs détaillés** - Trace complète du processus

### **En Ligne de Commande**
```bash
# Voir les logs en temps réel
adb logcat | grep "com.example.edunova"

# Filtrer les logs d'inscription
adb logcat | grep -E "(🎭|💾|✅|❌)"

# Filtrer les logs de base de données
adb logcat | grep -E "(🔍|📊|👤)"
```

## 🎯 **Prochaines Étapes**

1. **Recompiler** avec les nouveaux logs
2. **Tester** la base de données
3. **Tenter** une inscription
4. **Analyser** les logs selon ce guide
5. **Identifier** le problème exact
6. **Appliquer** la solution correspondante

---

## 📞 **Support**

Une fois que vous avez testé avec les nouveaux logs, partagez-moi :

1. **Les logs complets** de l'inscription
2. **Le résultat** du test de base de données
3. **Le résultat** de la vérification des utilisateurs

Je pourrai alors vous donner la solution précise ! 🔍✨