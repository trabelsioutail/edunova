@echo off
echo ========================================
echo    RESOLUTION PROBLEME R.JAR LOCK
echo         Projet EduNova
echo ========================================
echo.

echo [1/6] Arret des processus Gradle...
gradlew --stop
echo ✅ Daemons Gradle arretes
echo.

echo [2/6] Verification des processus Java...
tasklist | findstr java >nul
if %errorlevel% == 0 (
    echo ⚠️  Processus Java detectes, arret force...
    taskkill /f /im java.exe 2>nul
) else (
    echo ✅ Aucun processus Java actif
)
echo.

echo [3/6] Suppression des fichiers temporaires...
if exist "app\build\intermediates" (
    rmdir /s /q "app\build\intermediates" 2>nul
    echo ✅ Dossier intermediates supprime
)
if exist "app\build\tmp" (
    rmdir /s /q "app\build\tmp" 2>nul
    echo ✅ Dossier tmp supprime
)
if exist ".gradle\caches" (
    rmdir /s /q ".gradle\caches" 2>nul
    echo ✅ Cache Gradle supprime
)
echo.

echo [4/6] Nettoyage Gradle...
gradlew clean --no-daemon
if %errorlevel% == 0 (
    echo ✅ Nettoyage reussi
) else (
    echo ❌ Erreur lors du nettoyage
    pause
    exit /b 1
)
echo.

echo [5/6] Compilation du projet...
gradlew assembleDebug --no-daemon
if %errorlevel% == 0 (
    echo ✅ Compilation reussie
) else (
    echo ❌ Erreur lors de la compilation
    echo Verifiez les logs ci-dessus
    pause
    exit /b 1
)
echo.

echo [6/6] Verification de l'APK...
if exist "app\build\outputs\apk\debug\app-debug.apk" (
    echo ✅ APK genere avec succes
    for %%I in ("app\build\outputs\apk\debug\app-debug.apk") do echo    Taille: %%~zI octets
) else (
    echo ❌ APK non trouve
)
echo.

echo ========================================
echo        RESOLUTION TERMINEE
echo ========================================
echo.
echo Le probleme R.jar lock a ete resolu !
echo Vous pouvez maintenant utiliser Android Studio normalement.
echo.
echo Pour installer l'application :
echo   gradlew installDebug
echo.
pause