
@echo off
title Online Quiz Launcher

cd /d "%~dp0"

echo ==================================
echo   ONLINE QUIZ APPLICATION
echo ==================================

echo.
echo Starting Spring Boot server...

start "Quiz Server" cmd /k "call mvnw.cmd spring-boot:run"

echo.
echo Waiting for the server to start...

:CHECK_SERVER
powershell -NoProfile -Command "try { $r = Invoke-WebRequest -Uri 'http://localhost:8080/' -UseBasicParsing -TimeoutSec 2; exit 0 } catch { exit 1 }" >nul 2>&1

if errorlevel 1 (
    timeout /t 3 /nobreak >nul
    goto CHECK_SERVER
)

echo Server is ready!
echo Opening Online Quiz website...

start "" "http://localhost:8080/"

exit
