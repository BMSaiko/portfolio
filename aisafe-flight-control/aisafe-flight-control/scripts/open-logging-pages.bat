@echo off
REM US090 - Abrir paginas do Logging Server no browser
REM Execute: scripts\open-logging-pages.bat

set "BROWSER="

if exist "%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe" (
    set "BROWSER=%ProgramFiles(x86)%\Microsoft\Edge\Application\msedge.exe"
)

if not defined BROWSER (
    if exist "%ProgramFiles%\Microsoft\Edge\Application\msedge.exe" (
        set "BROWSER=%ProgramFiles%\Microsoft\Edge\Application\msedge.exe"
    )
)

if not defined BROWSER (
    if exist "%LOCALAPPDATA%\Google\Chrome\Application\chrome.exe" (
        set "BROWSER=%LOCALAPPDATA%\Google\Chrome\Application\chrome.exe"
    )
)

echo.
echo ============================================================
echo   US090 - Logging Server Web Pages
echo ============================================================
echo.

netstat -an | findstr ":8080" | findstr LISTENING >nul 2>&1
if errorlevel 1 (
    echo [WARN] Logging Server does not appear to be running on port 8080!
    echo         Start it first: .\\scripts\\run-us078-us090.bat
    echo.
    pause
    exit /b 1
)

if defined BROWSER (
    start "" "%BROWSER%" http://localhost:8080/
    timeout /t 2 /nointerrupt >nul
    start "" "%BROWSER%" http://localhost:8080/active
) else (
    start http://localhost:8080/
    timeout /t 2 /nointerrupt >nul
    start http://localhost:8080/active
)

echo Pages opened:
echo   http://localhost:8080/       (Access Logs)
echo   http://localhost:8080/active (Active Sessions)
echo.
pause
