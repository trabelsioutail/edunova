# 🚀 Guide de Démarrage - Application EduNova

## ✅ **Compilation Réussie !**

Votre application Android EduNova a été **compilée avec succès** !

- ✅ **BUILD SUCCESSFUL** en 1m 6s
- ✅ **APK généré** : `app/build/outputs/apk/debug/app-debug.apk` (16.8 MB)
- ✅ **Aucune erreur de compilation**
- ✅ **Architecture MVVM complète**

## 📱 **Options pour Démarrer l'Application**

### **Option 1 : Émulateur Android (Recommandé)**

#### **1.1 Ouvrir Android Studio**
```bash
# Si Android Studio n'est pas ouvert
start android-studio
```

#### **1.2 Créer/Démarrer un Émulateur**
1. **Ouvrir Android Studio**
2. **Tools** → **AVD Manager**
3. **Create Virtual Device** (si pas d'émulateur)
   - Choisir un **Pixel 7** ou similaire
   - **API Level 34** (Android 14) ou plus récent
   - **x86_64** pour de meilleures performances
4. **Cliquer sur ▶️ (Play)** pour démarrer l'émulateur

#### **1.3 Installer l'Application**
Une fois l'émulateur démarré :
```bash
./gradlew installDebug
```

### **Option 2 : Appareil Physique Android**

#### **2.1 Activer le Mode Développeur**
1. **Paramètres** → **À propos du téléphone**
2. **Appuyer 7 fois** sur "Numéro de build"
3. **Retour** → **Options pour les développeurs**
4. **Activer** "Débogage USB"

#### **2.2 Connecter l'Appareil**
1. **Connecter** le téléphone via USB
2. **Autoriser** le débogage USB sur le téléphone
3. **Vérifier** la connexion :
```bash
./gradlew installDebug
```

### **Option 3 : Installation Manuelle de l'APK**

#### **3.1 Transférer l'APK**
L'APK se trouve dans : `app/build/outputs/apk/debug/app-debug.apk`

**Sur émulateur :**
- **Glisser-déposer** l'APK dans l'émulateur

**Sur appareil physique :**
- **Copier** l'APK sur le téléphone
- **Ouvrir** avec un gestionnaire de fichiers
- **Installer** (autoriser les sources inconnues si nécessaire)

## 🔧 **Commandes Utiles**

### **Compilation et Installation**
```bash
# Nettoyer et compiler
./gradlew clean assembleDebug

# Installer sur l'appareil connecté
./gradlew installDebug

# Désinstaller l'application
./gradlew uninstallDebug

# Voir les appareils connectés (si ADB est configuré)
adb devices
```

### **Logs et Débogage**
```bash
# Voir les logs de l'application (si ADB configuré)
adb logcat | grep "com.example.edunova"

# Logs spécifiques à notre app
adb logcat -s "EduNova"
```

## 📊 **Fonctionnalités de l'Application**

### **🔐 Authentification**
- **Écran de connexion** avec email/mot de passe
- **Écran d'inscription** avec validation
- **Gestion de session** persistante
- **Déconnexion** sécurisée

### **🏠 Écran Principal**
- **Profil utilisateur** avec informations
- **Liste des cours** avec actualisation
- **Navigation** intuitive
- **Gestion d'erreurs** complète

### **🧪 Outils de Diagnostic**
- **Bouton de test** de la base de données
- **Logs détaillés** pour déboguer l'inscription
- **Messages d'erreur** informatifs

## 🎯 **Test de l'Application**

### **Étape 1 : Premier Lancement**
1. **Ouvrir** l'application EduNova
2. **Vérifier** que l'écran de connexion s'affiche
3. **Tester** la navigation vers l'inscription

### **Étape 2 : Test de la Base de Données**
1. **Aller** sur l'écran d'inscription
2. **Cliquer** sur "🧪 Tester la base de données"
3. **Vérifier** le message de succès/erreur

### **Étape 3 : Test d'Inscription**
1. **Remplir** le formulaire d'inscription
2. **Cliquer** sur "S'inscrire"
3. **Observer** le comportement (succès/erreur)

### **Étape 4 : Test de Connexion**
1. **Utiliser** les identifiants créés
2. **Se connecter**
3. **Vérifier** l'accès à l'écran principal

## 🔍 **Analyse des Logs**

### **Ouvrir Logcat dans Android Studio**
1. **View** → **Tool Windows** → **Logcat**
2. **Filtrer** par : `com.example.edunova`
3. **Niveau** : Verbose
4. **Rechercher** les messages avec 🔍, ✅, ❌

### **Logs à Surveiller**
```
🧪 Test de connexion à la base de données...
✅ Base de données fonctionne correctement

🎯 AuthViewModel.register() - Début
📝 Données: firstName=John, lastName=Doe, email=john@example.com
🔍 AuthRepository.register() - Début inscription
📡 Réponse API: NetworkResult.Success(...)
✅ Utilisateur sauvegardé dans Room Database
```

## 🚨 **Résolution de Problèmes**

### **Problème : Émulateur ne démarre pas**
**Solution :**
- **Vérifier** que la virtualisation est activée dans le BIOS
- **Utiliser** un émulateur x86_64
- **Redémarrer** Android Studio

### **Problème : Installation échoue**
**Solution :**
```bash
# Nettoyer et recompiler
./gradlew clean
./gradlew assembleDebug
./gradlew installDebug
```

### **Problème : Application plante au démarrage**
**Solution :**
- **Vérifier** les logs dans Logcat
- **Rechercher** les exceptions dans les logs
- **Tester** avec le bouton de diagnostic

## 🎉 **Prochaines Étapes**

1. **Démarrer** un émulateur Android
2. **Installer** l'application avec `./gradlew installDebug`
3. **Tester** les fonctionnalités principales
4. **Analyser** les logs pour résoudre le problème d'inscription
5. **Optimiser** l'application selon les résultats

---

## 📞 **Support**

Si vous rencontrez des problèmes :

1. **Partager** les logs de Logcat
2. **Décrire** le comportement observé
3. **Indiquer** à quelle étape le problème survient

**Votre application EduNova est prête à être testée ! 🚀**