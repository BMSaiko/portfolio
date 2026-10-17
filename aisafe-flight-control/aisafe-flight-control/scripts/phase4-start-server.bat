@echo off
REM ============================================================
REM  Phase 4: Start TCP Server locally with cloud logging
REM  Finds Maven, starts server, waits for port 2002
REM ============================================================
setlocal EnableDelayedExpansion
set "PATH=%PATH%;%SystemRoot%\System32"

REM --- Navigate to project root ---
cd /d "%~dp0.."
set "PROJECT_ROOT=%CD%"

set "CLOUD_VS_IP=10.9.21.233"

REM --- Find Maven ---
set "MVN=mvn"
%SystemRoot%\System32\where.exe mvn >nul 2>&1
if errorlevel 1 (
    if exist "C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd" (
        set "MVN=C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd"
    ) else (
        echo   [ERRO] Maven not found!
        pause & exit /b 1
    )
)

echo   Starting TCP Server -^> Cloud Logging at %CLOUD_VS_IP%:9999
start "US078-Server" cmd /d /c "cd /d %PROJECT_ROOT% && "%MVN%" exec:java -pl aisafe.app.server -Dlogging.host=%CLOUD_VS_IP% -o"

echo   Waiting for port 2002...
set "PORT_READY=0"
for /L %%i in (1,1,90) do (
    %SystemRoot%\System32\netstat.exe -an 2>nul | %SystemRoot%\System32\findstr.exe "2002" | %SystemRoot%\System32\findstr.exe "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (
        set "PORT_READY=1"
        goto :port_ok
    )
    %SystemRoot%\System32\ping.exe -n 2 127.0.0.1 >nul 2>&1
)
:port_ok
if "!PORT_READY!"=="1" (
    echo   OK: TCP Server listening on port 2002
) else (
    echo   [ERRO] Timeout waiting for TCP port 2002!
    echo   Killing Java processes...
    %SystemRoot%\System32\taskkill.exe /f /im java.exe >nul 2>&1
    pause
    exit /b 1
)

exit /b 0
