@echo off
REM ============================================================
REM  AISafe Flight Control - Bootstrap
REM  Checks Java/Maven availability and project structure
REM ============================================================
setlocal EnableDelayedExpansion

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
echo   AISafe Flight Control - Bootstrap
echo ============================================================
echo.

REM --- Check Java ---
java -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Java is not installed or not available in PATH.
    pause
    exit /b 1
)
echo Java version:
java -version 2>&1 | findstr /i "version"
echo.

REM --- Check Maven ---
call %MVN% -version >nul 2>&1
if errorlevel 1 (
    echo [ERRO] Maven is not installed or not available in PATH.
    pause
    exit /b 1
)
echo Maven version:
call %MVN% -version 2>&1 | findstr /i "Apache Maven"
echo.

echo Project root: %PROJECT_ROOT%
echo.
echo Bootstrap completed successfully.

pause
exit /b 0