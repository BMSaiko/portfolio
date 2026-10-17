@echo off
REM ============================================================
REM  US078 ^& US090 - Full Clean Build Script (Windows)
REM  Cleans and compiles everything needed for US078 and US090
REM  Execute: scripts\build-us078-us090.bat
REM ============================================================
setlocal

REM --- Resolve paths ---
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
for %%F in ("%SCRIPT_DIR%") do set "PROJECT_ROOT=%%~dpF"
set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"
for %%F in ("%PROJECT_ROOT%") do set "PARENT_DIR=%%~dpF"
set "PARENT_DIR=%PARENT_DIR:~0,-1%"
set "LOGGING_SERVER_ROOT=%PARENT_DIR%\aisafe-logging-server"

REM --- Detect mvn or mvnw ---
set "MVN=mvn"
if exist "%PROJECT_ROOT%mvnw.cmd" set "MVN=%PROJECT_ROOT%mvnw.cmd"

REM --- Check aisafe-logging-server exists ---
if not exist "%LOGGING_SERVER_ROOT%\pom.xml" (
    echo.
    echo [ERRO] aisafe-logging-server not found at: %LOGGING_SERVER_ROOT%
    echo Make sure the aisafe-logging-server repo exists next to aisafe-flight-control
    pause
    exit /b 1
)

echo.
echo ============================================================
echo   US078 ^& US090 - Full Clean Build
echo ============================================================
echo.

REM ============================================================
REM  STEP 1 - Clean aisafe-flight-control
REM ============================================================
echo [1/4] Cleaning aisafe-flight-control ...
cd /d "%PROJECT_ROOT%"
call %MVN% clean -q
if errorlevel 1 (
    echo [ERRO] Failed to clean aisafe-flight-control
    pause
    exit /b 1
)
echo         OK

REM ============================================================
REM  STEP 2 - Clean aisafe-logging-server
REM ============================================================
echo.
echo [2/4] Cleaning aisafe-logging-server ...
cd /d "%LOGGING_SERVER_ROOT%"
call mvn clean -q
if errorlevel 1 (
    echo [ERRO] Failed to clean aisafe-logging-server
    pause
    exit /b 1
)
echo         OK

REM ============================================================
REM  STEP 3 - Compile aisafe-flight-control (all modules)
REM ============================================================
echo.
echo [3/4] Compiling aisafe-flight-control (all modules) ...
cd /d "%PROJECT_ROOT%"
call %MVN% clean install -DskipTests
if errorlevel 1 (
    echo [ERRO] Failed to compile aisafe-flight-control
    pause
    exit /b 1
)
echo         OK

REM ============================================================
REM  STEP 4 - Compile aisafe-logging-server (standalone)
REM ============================================================
echo.
echo [4/4] Compiling aisafe-logging-server (standalone) ...
cd /d "%LOGGING_SERVER_ROOT%"
call %MVN% clean package -DskipTests
if errorlevel 1 (
    echo [ERRO] Failed to compile aisafe-logging-server
    pause
    exit /b 1
)
echo         OK

REM ============================================================
REM  DONE
REM ============================================================
echo.
echo ============================================================
echo   BUILD COMPLETE - US078 ^& US090
echo ============================================================
echo.
echo   Modules compiled:
echo     [OK] aisafe.core              (domain, remotelogging)
echo     [OK] aisafe.persistence.impl  (JPA repositories)
echo     [OK] aisafe.bootstrap         (reference data)
echo     [OK] aisafe.app.server        (TCP server US078, port 2002)
echo     [OK] aisafe.app.company.console (TCP client US078)
echo     [OK] aisafe-logging-server    (UDP logging US090, standalone)
echo.
echo   Next step:
echo     .\run-us078-us090.bat
echo.

pause
exit /b 0