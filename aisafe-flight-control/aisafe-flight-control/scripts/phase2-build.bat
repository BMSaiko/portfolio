@echo off
REM ============================================================
REM  Phase 2: Build all modules
REM  Portable: uses cd to navigate, no absolute paths
REM ============================================================
setlocal
set "PATH=%PATH%;%SystemRoot%\System32"

REM --- Find Maven (common install locations) ---
set "MVN=mvn"
%SystemRoot%\System32\where.exe mvn >nul 2>&1
if errorlevel 1 (
    if exist "C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd" (
        set "PATH=%PATH%;C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin"
        set "MVN=mvn"
    ) else (
        echo   [ERRO] Maven not found! Add Maven to PATH.
        pause & exit /b 1
    )
)

REM --- Navigate to project root (parent of scripts dir) ---
cd /d "%~dp0.."
set "PROJECT_ROOT=%CD%"

REM --- Navigate to logging server (sibling of project dir) ---
cd /d "%PROJECT_ROOT%\..\aisafe-logging-server"
set "LOGGING_SERVER_ROOT=%CD%"

if not exist "pom.xml" (
    echo   [ERRO] aisafe-logging-server not found at: %LOGGING_SERVER_ROOT%
    pause & exit /b 1
)

echo   Compiling aisafe-flight-control...
cd /d "%PROJECT_ROOT%"
mvn compile -pl aisafe.core,aisafe.persistence.impl,aisafe.bootstrap,aisafe.app.server,aisafe.app.company.console -am
if errorlevel 1 (
    echo   [ERRO] aisafe-flight-control build failed!
    taskkill /f /im java.exe >nul 2>&1
    pause & exit /b 1
)
echo   OK: aisafe-flight-control compiled

echo   Packaging aisafe-logging-server...
cd /d "%LOGGING_SERVER_ROOT%"
mvn package -DskipTests -Dmaven.test.skip=true
if errorlevel 1 (
    echo   [ERRO] aisafe-logging-server build failed!
    taskkill /f /im java.exe >nul 2>&1
    pause & exit /b 1
)
echo   OK: aisafe-logging-server built

exit /b 0
