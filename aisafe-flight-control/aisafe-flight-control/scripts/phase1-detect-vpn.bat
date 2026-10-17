@echo off
REM ============================================================
REM  Phase 1: Detect VPN and resolve Cloud VS IP
REM  Portable: uses %SystemRoot% and relative paths only
REM ============================================================
setlocal EnableDelayedExpansion
set "PATH=%PATH%;%SystemRoot%\System32;%SystemRoot%\System32\OpenSSH"
set "CLOUD_VS_HOST=vs489.dei.isep.ipp.pt"
set "CLOUD_VS_IP=10.9.21.233"

echo [1/2] Detecting VPN...
set "VPN_IP="
for /f "tokens=2 delims=:" %%A in ('ipconfig ^| findstr /i "IPv4" ^| findstr /i "10\."') do (
    set "VPN_IP=%%A" & set "VPN_IP=!VPN_IP: =!" & goto :vpn_done
)
:vpn_done
if not defined VPN_IP (
    echo   [ERRO] VPN NOT DETECTED! Connect VPN first: deinet.dei.isep.ipp.pt:443
    pause & exit /b 1
)
echo   VPN IP: !VPN_IP!

echo [2/2] Resolving Cloud VS...
for /f "tokens=2 delims=[]" %%A in ('ping -n 1 -w 3000 !CLOUD_VS_HOST! 2^>nul ^| findstr /i "Pinging"') do (
    set "CLOUD_VS_IP=%%A"
)
echo   Cloud VS: !CLOUD_VS_IP!
ping -n 1 -w 3000 !CLOUD_VS_IP! >nul 2>&1
if errorlevel 1 (echo   WARNING: Cannot ping Cloud VS) else (echo   OK: Cloud VS reachable)
echo.

endlocal & set "VPN_IP=%VPN_IP%" & set "CLOUD_VS_IP=%CLOUD_VS_IP%"
exit /b 0
