package com.example.edunova

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class avec Hilt (Critère I.3 - Injection de dépendances)
 */
@HiltAndroidApp
class MyApp : Application() {

    companion object {
        lateinit var instance: MyApp
            private set
        
        private const val TAG = "EduNovaApp"
    }

    override fun onCreate() {
        super.onCreate()
        
        try {
            instance = this
            Log.d(TAG, "✅ Application EduNova initialisée avec succès")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Erreur lors de l'initialisation de l'application", e)
            // Ne pas faire crash l'app, juste logger l'erreur
        }
    }
}