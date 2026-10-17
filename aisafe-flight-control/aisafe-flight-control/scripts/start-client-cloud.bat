@echo off
REM ============================================================
REM  US078 - Start TCP Client (Air Transport Company App)
REM  Connects to local TCP Server on 127.0.0.1:2002
REM ============================================================
setlocal

REM --- Fix PATH for cmd.exe ---
set "PATH=%PATH%;C:\Windows\System32;C:\Windows\System32\OpenSSH;C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin"

set "PROJECT_ROOT=%~dp0.."
set "MVN=C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd"

echo.
echo ============================================================
echo   US078 - TCP Client (Air Transport Company App)
echo ============================================================
echo.
echo   Connecting to local TCP Server: 127.0.0.1:2002
echo.
echo   Make sure the TCP Server is already running!
echo   (Run start-server-cloud.bat first)
echo.

REM --- Check if TCP Server is reachable on port 2002 ---
echo Checking TCP Server on port 2002...
netstat -an | findstr ":2002 " | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo.
    echo   WARNING: No server listening on port 2002!
    echo   Please start the TCP Server first:
    echo     scripts\start-server-cloud.bat
    echo.
    pause
    exit /b 1
)
echo   OK: TCP Server is listening on port 2002.
echo.

REM --- Login instructions ---
echo ============================================================
echo   LOGIN CREDENTIALS (from bootstrap)
echo ============================================================
echo.
echo   Email:    atcc@airline.com
echo   Password: Atcc1234
echo.
echo   Other users:
echo     admin@aisafe.com      / Admin123
echo     backoffice@aisafe.com / Back1234
echo     weather@aisafe.com    / Weather123
echo     pilot@aisafe.com      / Pilot123
echo     fco@control.com       / Fco12345
echo.

cd /d "%PROJECT_ROOT%"
"%MVN%" exec:java -pl aisafe.app.company.console -o

pause
