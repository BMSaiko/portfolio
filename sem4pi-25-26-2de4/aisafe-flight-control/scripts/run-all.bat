@echo off
REM ============================================================
REM  AISafe Flight Control - Run All
REM  Launches the Backoffice application
REM  Execute: scripts\run-all.bat
REM ============================================================
setlocal

REM --- Resolve paths ---
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

echo.
echo ============================================================
echo   AISafe Flight Control - Run All
echo ============================================================
echo.
echo Starting Backoffice application...
echo.

call "%SCRIPT_DIR%run-backoffice.bat"

exit /b 0