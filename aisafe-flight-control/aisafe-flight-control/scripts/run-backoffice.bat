@echo off
REM ============================================================
REM  AISafe Flight Control - Backoffice
REM  Builds required modules and launches the Backoffice console
REM  Execute: scripts\run-backoffice.bat
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
echo   AISafe Flight Control - Backoffice
echo ============================================================
echo.

cd /d "%PROJECT_ROOT%"

echo Building required modules...
call %MVN% -DskipTests install
if errorlevel 1 (
    echo.
    echo [ERRO] Build failed! Cannot start Backoffice.
    pause
    exit /b 1
)

echo.
echo Starting Backoffice application...
echo.

call %MVN% exec:java -pl aisafe.app.backoffice.console

pause
exit /b 0