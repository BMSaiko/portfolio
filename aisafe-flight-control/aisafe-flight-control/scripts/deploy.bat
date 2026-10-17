@echo off
REM ============================================================
REM  AISafe Flight Control - Deploy
REM  Full pipeline: bootstrap ^> clean ^> test ^> build
REM ============================================================
setlocal

REM --- Resolve paths ---
set "SCRIPT_DIR=%~dp0"
set "SCRIPT_DIR=%SCRIPT_DIR:~0,-1%"

echo.
echo ============================================================
echo   AISafe Flight Control - Deploy
echo ============================================================
echo.

call "%SCRIPT_DIR%bootstrap.bat"
if errorlevel 1 goto :deploy_fail

call "%SCRIPT_DIR%clean.bat"
if errorlevel 1 goto :deploy_fail

call "%SCRIPT_DIR%test.bat"
if errorlevel 1 goto :deploy_fail

call "%SCRIPT_DIR%build.bat"
if errorlevel 1 goto :deploy_fail

echo.
echo ============================================================
echo   Deployment completed successfully.
echo ============================================================
echo.
echo To run the Backoffice application:
echo   .\scripts\run-backoffice.bat
echo.

pause
exit /b 0

:deploy_fail
echo.
echo [ERRO] Deployment failed!
pause
exit /b 1