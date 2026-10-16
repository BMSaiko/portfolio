@echo off
REM ============================================================
REM  AISafe Flight Control - Clean
REM  Removes all build artifacts (target/ directories)
REM  Execute: scripts\clean.bat
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
echo   AISafe Flight Control - Clean
echo ============================================================
echo.

cd /d "%PROJECT_ROOT%"

call %MVN% clean
if errorlevel 1 (
    echo.
    echo [ERRO] Clean failed!
    pause
    exit /b 1
)

echo.
echo Clean completed successfully.

pause
exit /b 0