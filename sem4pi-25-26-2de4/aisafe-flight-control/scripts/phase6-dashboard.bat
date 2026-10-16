@echo off
REM ============================================================
REM  Phase 6: Open browser dashboard via SSH tunnel
REM  Checks if Logging Server is running, restarts if needed
REM ============================================================
setlocal
set "PATH=%PATH%;%SystemRoot%\System32;%SystemRoot%\System32\OpenSSH"
set "CLOUD_VS_HOST=vs489"

REM --- Check if Logging Server is running on Cloud VS ---
echo   Checking Logging Server on Cloud VS...
ssh %CLOUD_VS_HOST% "pgrep -f aisafe-logging-server" >nul 2>&1
if errorlevel 1 (
    echo   Logging Server not running. Restarting...
    ssh %CLOUD_VS_HOST% "/opt/aisafe-logging-server/start-logging-server-vs.sh start"
    %SystemRoot%\System32\ping.exe -n 5 127.0.0.1 >nul 2>&1
)

REM --- Create SSH tunnel ---
echo   Creating SSH tunnel (localhost:8080 -^> Cloud VS:8080)...
start "SSH-Tunnel" cmd /d /c "ssh -L 8080:localhost:8080 -N -T %CLOUD_VS_HOST%"

echo   Waiting for tunnel...
%SystemRoot%\System32\ping.exe -n 5 127.0.0.1 >nul 2>&1

REM --- Test tunnel ---
echo   Testing tunnel...
ssh %CLOUD_VS_HOST% "wget -qO- http://localhost:8080/api/logs 2>/dev/null | head -c 100" >nul 2>&1
if errorlevel 1 (
    echo   WARNING: Tunnel may not be ready. Retrying...
    %SystemRoot%\System32\ping.exe -n 5 127.0.0.1 >nul 2>&1
)

echo   Opening browser...
start "" http://localhost:8080/

echo   Dashboard: http://localhost:8080/

exit /b 0
