@echo off
REM ============================================================
REM  US078 ^& US090 - Full Run Script (Windows, No Python)
REM  Starts Logging Server, TCP Server, TCP Client sequentially
REM  with port-wait between each step.
REM  On exit (Ctrl+C / close) kills all Java processes ^& browser tabs.
REM ============================================================
setlocal EnableDelayedExpansion

REM --- Resolve paths ---
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"
for %%F in ("%SCRIPT_DIR%") do set "PROJECT_ROOT=%%~dpF"
set "PROJECT_ROOT=%PROJECT_ROOT:~0,-1%"
for %%F in ("%PROJECT_ROOT%") do set "PARENT_DIR=%%~dpF"
set "PARENT_DIR=%PARENT_DIR:~0,-1%"
set "LOGGING_SERVER_ROOT=%PARENT_DIR%\aisafe-logging-server"

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
echo   US078 ^& US090 - Sequential Start (port-wait)
echo ============================================================
echo.
echo   [1] US090 Logging Server  (UDP :9999, HTTP :8080)
echo   [2] US078 TCP Server      (TCP :2002)
echo   [3] US078 TCP Client      (Air Transport Company App)
echo.

REM ============================================================
REM  STEP 1 - Start US090 Logging Server (background)
REM ============================================================
echo [1/3] Starting US090 Logging Server (UDP :9999, HTTP :8080) ...
cd /d "%LOGGING_SERVER_ROOT%"
start "US090-LoggingServer" cmd /d /c "mvn exec:java -q"
echo         Waiting for HTTP port 8080 ...

set "PORT8080_READY=0"
for /L %%i in (1,1,60) do (
    netstat -an | findstr ":8080 " | findstr "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        set "PORT8080_READY=1"
        goto :port8080_done
    )
    timeout /t 1 /nobreak >nul
)
echo [ERRO] Timeout waiting for HTTP port 8080
goto :cleanup

:port8080_done
echo         Logging Server is ready on port 8080.

REM ============================================================
REM  STEP 2 - Start US078 TCP Server (background)
REM ============================================================
echo.
echo [2/3] Starting US078 TCP Server (TCP :2002) ...
cd /d "%PROJECT_ROOT%"
start "US078-TcpServer" cmd /d /c "mvn exec:java -pl aisafe.app.server -q"
echo         Waiting for TCP port 2002 ...

set "PORT2002_READY=0"
for /L %%i in (1,1,60) do (
    netstat -an | findstr ":2002 " | findstr "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        set "PORT2002_READY=1"
        goto :port2002_done
    )
    timeout /t 1 /nobreak >nul
)
echo [ERRO] Timeout waiting for TCP port 2002
goto :cleanup

:port2002_done
echo         TCP Server is ready on port 2002.

REM ============================================================
REM  STEP 3 - Start US078 TCP Client (foreground, interactive)
REM ============================================================
echo.
echo [3/3] Starting US078 TCP Client (Air Transport Company App) ...
echo         Connecting to 127.0.0.1:2002 ...
echo.
echo ============================================================
echo   All services started. Use the client menu to interact.
echo   Press Ctrl+C or close this window to stop everything.
echo ============================================================
echo.

REM --- Open browser pages for Logging Server ---
start "" http://localhost:8080/
timeout /t 2 /nobreak >nul
start "" http://localhost:8080/active

REM --- Run client in foreground (this blocks) ---
set "CLIENT_EXIT=0"
cd /d "%PROJECT_ROOT%"
call mvn exec:java -pl aisafe.app.company.console
set "CLIENT_EXIT=%errorlevel%"

echo.
echo TCP Client exited (exit code: %CLIENT_EXIT%).
echo Initiating cleanup...

REM ============================================================
REM  CLEANUP - Kill all Java processes and close browser tabs
REM ============================================================
:cleanup
echo.
echo ============================================================
echo   Cleanup - Stopping all services ...
echo ============================================================

REM --- Kill all java.exe processes ---
echo         Killing java processes ...
taskkill /f /im java.exe >nul 2>&1
taskkill /f /im javaw.exe >nul 2>&1

REM --- Close browser tabs for localhost:8080 (best effort) ---
echo         Closing browser tabs (localhost:8080) ...
powershell -NoProfile -ExecutionPolicy Bypass -Command "$shell = New-Object -ComObject Shell.Application; $tabs = $shell.Windows(); foreach($t in $tabs) { if($t.LocationURL -like '*localhost:8080*') { $t.Quit() } }" >nul 2>&1

REM --- Kill leftover cmd windows we opened ---
echo         Closing terminal windows ...
taskkill /fi "WINDOWTITLE eq US090-LoggingServer" /f >nul 2>&1
taskkill /fi "WINDOWTITLE eq US078-TcpServer" /f >nul 2>&1

REM --- Remove Python cache leftovers if any ---
echo         Cleaning temp files ...
if exist "%SCRIPT_DIR%\__pycache__" (
    rmdir /s /q "%SCRIPT_DIR%\__pycache__" >nul 2>&1
)

echo.
echo ============================================================
echo   All services stopped. Goodbye.
echo ============================================================
echo.

pause
exit /b %CLIENT_EXIT%
