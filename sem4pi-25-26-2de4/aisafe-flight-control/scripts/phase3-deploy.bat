@echo off
REM ============================================================
REM  Phase 3: Deploy JAR to Cloud VS
REM  Portable: uses cd to navigate, %USERPROFILE% for SSH key
REM ============================================================
setlocal
set "PATH=%PATH%;%SystemRoot%\System32;%SystemRoot%\System32\OpenSSH"

REM --- Navigate to logging server (sibling of project dir) ---
cd /d "%~dp0..\..\aisafe-logging-server"
set "LOGGING_SERVER_ROOT=%CD%"

REM --- Use SSH config (Host vs489) for authentication ---
set "CLOUD_VS_HOST=vs489"

echo   Copying JAR to Cloud VS...
scp "%LOGGING_SERVER_ROOT%\target\aisafe-logging-server-1.0.0.jar" %CLOUD_VS_HOST%:/opt/aisafe-logging-server/
if errorlevel 1 (
    echo   [ERRO] SCP failed!
    taskkill /f /im java.exe >nul 2>&1
    pause & exit /b 1
)
echo   OK: JAR copied

echo   Restarting Logging Server on Cloud VS...
ssh %CLOUD_VS_HOST% "pkill -f aisafe-logging-server 2>nul; sleep 1; /opt/aisafe-logging-server/start-logging-server-vs.sh start"
if errorlevel 1 (
    echo   [ERRO] Failed to restart Logging Server!
    taskkill /f /im java.exe >nul 2>&1
    pause & exit /b 1
)
echo   OK: Logging Server restarted

exit /b 0
