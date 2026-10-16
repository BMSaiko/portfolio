@echo off
REM ============================================================
REM  US078 + US090 - Complete Cloud Workflow (Windows)
REM  Master script - portable, uses %SystemRoot% for PATH
REM ============================================================
setlocal EnableDelayedExpansion

REM --- Portable PATH fix ---
set "PATH=%PATH%;%SystemRoot%\System32;%SystemRoot%\System32\OpenSSH"

REM --- Ensure we're in the scripts directory ---
cd /d "%~dp0"

echo.
echo ============================================================
echo   US078 + US090 - Complete Cloud Workflow
echo ============================================================
echo.

goto :main

:cleanup
echo.
echo ===== Cleanup: Killing Java processes =====
taskkill /f /im java.exe >nul 2>&1
pause
exit /b 1

:main

echo ===== Phase 1: VPN Detection =====
call phase1-detect-vpn.bat
if errorlevel 1 (echo [ERRO] Phase 1 failed. & goto :cleanup)
echo.

echo ===== Phase 2: Build =====
call phase2-build.bat
if errorlevel 1 (echo [ERRO] Phase 2 failed. & goto :cleanup)
echo.

echo ===== Phase 3: Deploy =====
call phase3-deploy.bat
if errorlevel 1 (echo [ERRO] Phase 3 failed. & goto :cleanup)
echo.

echo ===== Phase 4: Start TCP Server =====
call phase4-start-server.bat
if errorlevel 1 (echo [ERRO] Phase 4 failed. & goto :cleanup)
echo.

echo ===== Phase 5: Start TCP Client =====
call phase5-start-client.bat
echo.

echo ===== Phase 6: Open Dashboard =====
call phase6-dashboard.bat
echo.

echo ============================================================
echo   Workflow complete!
echo   Dashboard: http://localhost:8080/
echo.
echo   To stop everything, run:
echo     scripts\cleanup-all.bat
echo.
pause
