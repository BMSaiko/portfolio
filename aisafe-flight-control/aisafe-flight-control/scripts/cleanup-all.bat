@echo off
set "PATH=%PATH%;%SystemRoot%\System32"
REM ============================================================
REM  Cleanup All - Kills all Java processes and closes terminals
REM  Run this to stop everything: server, client, tunnel, etc.
REM ============================================================
echo.
echo ===== Killing all Java processes...
%SystemRoot%\System32\taskkill.exe /f /im java.exe 2>nul
%SystemRoot%\System32\taskkill.exe /f /im javaw.exe 2>nul
echo   Done.

echo.
echo ===== Closing terminal windows...
%SystemRoot%\System32\taskkill.exe /fi "IMAGENAME eq cmd.exe" /fi "WINDOWTITLE eq US078-Server" /f 2>nul
%SystemRoot%\System32\taskkill.exe /fi "IMAGENAME eq cmd.exe" /fi "WINDOWTITLE eq US078-Client" /f 2>nul
%SystemRoot%\System32\taskkill.exe /fi "IMAGENAME eq cmd.exe" /fi "WINDOWTITLE eq SSH-Tunnel" /f 2>nul
%SystemRoot%\System32\taskkill.exe /fi "IMAGENAME eq cmd.exe" /fi "WINDOWTITLE eq Selecionar SSH-Tunnel" /f 2>nul
%SystemRoot%\System32\taskkill.exe /fi "IMAGENAME eq conhost.exe" /fi "WINDOWTITLE eq SSH-Tunnel" /f 2>nul
echo   Done.

echo.
echo Cleanup complete.
exit /b 0
