@echo off
REM ============================================================
REM  Phase 5: Start TCP Client (Air Transport Company App)
REM  Waits for server port 2002, then opens interactive client
REM ============================================================
setlocal
set "PATH=%PATH%;%SystemRoot%\System32"

REM --- Find Maven (same as phase2) ---
where mvn >nul 2>&1
if errorlevel 1 (
    if exist "C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd" (
        set "PATH=%PATH%;C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin"
    ) else (
        echo   [ERRO] Maven not found! Add Maven to PATH.
        pause & exit /b 1
    )
)

REM --- Derive paths ---
cd /d "%~dp0.."
set "PROJECT_ROOT=%CD%"

REM --- Wait for TCP Server port 2002 ---
echo   Waiting for TCP Server on port 2002...
set "PORT_READY=0"
for /L %%i in (1,1,30) do (
    %SystemRoot%\System32\netstat.exe -an 2>nul | %SystemRoot%\System32\findstr.exe "2002" | %SystemRoot%\System32\findstr.exe "LISTENING" >nul 2>&1
    if !errorlevel! equ 0 (set "PORT_READY=1" & goto :port_ok)
    %SystemRoot%\System32\ping.exe -n 2 127.0.0.1 >nul 2>&1
)
:port_ok
if "!PORT_READY!"=="0" (
    echo   [WARNING] Port 2002 not detected. Client may not connect.
)

REM --- Start TCP Client in new window ---
echo   Starting TCP Client...
echo   Login: atcc@airline.com / Atcc1234
start "US078-Client" cmd /d /c "cd /d %PROJECT_ROOT% && echo ============================================================ && echo   US078 - Air Transport Company App && echo   Login: atcc@airline.com / Atcc1234 && echo ============================================================ && echo. && mvn exec:java -pl aisafe.app.company.console -o || pause"

exit /b 0
