package com.example.edunova.utils

object Constants {
    // Configuration API
    const val BASE_URL = "http://10.0.2.2:8080/" // Pour émulateur Android
    // const val BASE_URL = "http://192.168.1.XX/edunova_api/" // Pour téléphone réel
    // const val BASE_URL = "https://votre-domaine.com/api/" // Pour production
    
    // Mode de développement
    const val OFFLINE_MODE = true // ✅ Activer le mode hors ligne pour les tests
    const val MOCK_API_RESPONSES = true // ✅ Utiliser des réponses simulées
    
    // Rôles utilisateur
    object UserRoles {
        const val ADMIN = "admin"
        const val TEACHER = "enseignant"
        const val STUDENT = "etudiant"
    }
    
    // Niveaux d'étude
    object StudyLevels {
        const val L1 = "L1"
        const val L2 = "L2"
        const val L3 = "L3"
        const val M1 = "M1"
        const val M2 = "M2"
        const val DOCTORAT = "Doctorat"
    }
    
    // Rôles de profil
    object ProfileRoles {
        const val STUDENT = "student"
        const val TEACHER = "teacher"
    }
    
    // Messages d'erreur
    object ErrorMessages {
        const val NETWORK_ERROR = "Erreur de connexion réseau"
        const val UNKNOWN_ERROR = "Une erreur inattendue s'est produite"
        const val INVALID_CREDENTIALS = "Email ou mot de passe incorrect"
        const val USER_NOT_FOUND = "Utilisateur non trouvé"
        const val UNAUTHORIZED = "Accès non autorisé"
        const val SERVER_ERROR = "Erreur du serveur"
    }
    
    // Timeouts
    const val NETWORK_TIMEOUT = 30L // secondes
    const val READ_TIMEOUT = 30L // secondes
    const val WRITE_TIMEOUT = 30L // secondes
    
    // Données de test pour le mode hors ligne
    object MockData {
        const val TEST_EMAIL = "test@edunova.com"
        const val TEST_PASSWORD = "password123"
        const val TEST_TOKEN = "mock-jwt-token-123456789"
    }
}