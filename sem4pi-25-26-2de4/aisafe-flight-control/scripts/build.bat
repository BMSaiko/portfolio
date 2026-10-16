@echo off
REM ============================================================
REM  AISafe Flight Control - Build
REM  Clean compiles all modules (skipping tests)
REM  Execute: scripts\build.bat
REM ============================================================
setlocal

REM --- Resolve paths ---
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
for %%F in ("%SCRIPT_DIR%") do set "PROJECT_ROOT=%%~dpF"
set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"

REM --- Detect mvn or mvnw ---
set "MVN=mvn"
if exist "%PROJECT_ROOT%mvnw.cmd" set "MVN=%PROJECT_ROOT%mvnw.cmd"

echo.
echo ============================================================
echo   AISafe Flight Control - Build
echo ============================================================
echo.

cd /d "%PROJECT_ROOT%"

call %MVN% clean install -DskipTests
if errorlevel 1 (
    echo.
    echo [ERRO] Build failed!
    pause
    exit /b 1
)

echo.
echo Build completed successfully.

pause
exit /b 0