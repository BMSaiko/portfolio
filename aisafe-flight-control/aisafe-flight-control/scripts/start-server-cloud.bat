@echo off
REM ============================================================
REM  US078 + US090 - Start TCP Server with Cloud Logging
REM  Detects VPN IP and resolves Cloud VS IP dynamically
REM ============================================================
setlocal EnableDelayedExpansion

REM --- Fix PATH for cmd.exe ---
set "PATH=%PATH%;C:\Windows\System32;C:\Windows\System32\OpenSSH;C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin"

set "PROJECT_ROOT=%~dp0.."
set "MVN_PATH=C:\Program Files (x86)\Apache\maven\apache-maven-3.9.11\bin\mvn.cmd"
set "CLOUD_VS_HOST=vs489.dei.isep.ipp.pt"
set "CLOUD_VS_IP=10.9.21.233"

echo.
echo ============================================================
echo   US078 + US090 - TCP Server (Cloud Logging)
echo ============================================================
echo.

REM --- Step 1: Check VPN connection ---
echo [1/3] Checking VPN connection...
echo.
echo   Make sure your VPN is connected before proceeding.
echo   Supported VPNs: SoftEther, SSTP, OpenVPN
echo   VPN Server: deinet.dei.isep.ipp.pt:443
echo.
set /p "VPN_OK=   Is your VPN connected? (y/n): "
if /i not "!VPN_OK!"=="y" (
    echo.
    echo   Aborted. Please connect your VPN first.
    pause
    exit /b 1
)
echo.

REM --- Step 2: Detect VPN IP from ipconfig ---
echo [2/3] Detecting VPN IP...
set "VPN_IP="
REM Try to find IP on adapter with "VPN" in name
for /f "tokens=2 delims=:" %%A in ('ipconfig ^| findstr /i "IPv4" ^| findstr /i "10\."') do (
    set "VPN_IP=%%A"
    set "VPN_IP=!VPN_IP: =!"
    goto :vpn_ip_found
)
:vpn_ip_found
if defined VPN_IP (
    echo   VPN IP detected: !VPN_IP!
) else (
    echo   WARNING: No VPN IP found. Proceeding anyway...
)
echo.

REM --- Step 3: Resolve Cloud VS IP via DNS ---
echo [3/3] Resolving Cloud VS IP...
echo   Host: %CLOUD_VS_HOST%
for /f "tokens=2 delims=[]" %%A in ('ping -n 1 -w 3000 %CLOUD_VS_HOST% 2^>nul ^| findstr /i "Pinging"') do (
    set "CLOUD_VS_IP=%%A"
)
echo   Cloud VS IP: !CLOUD_VS_IP!
echo.

REM --- Verify connectivity to Cloud VS ---
echo   Testing connectivity to Cloud VS (!CLOUD_VS_IP%)...
ping -n 1 -w 3000 !CLOUD_VS_IP! >nul 2>&1
if errorlevel 1 (
    echo   WARNING: Cannot ping Cloud VS at !CLOUD_VS_IP!
    echo   The server may be down or firewall may block ICMP.
    echo.
    set /p "CONTINUE=   Continue anyway? (y/n): "
    if /i not "!CONTINUE!"=="y" (
        echo   Aborted.
        pause
        exit /b 1
    )
) else (
    echo   OK: Cloud VS is reachable.
)
echo.

REM --- Start TCP Server ---
echo ============================================================
echo   Starting TCP Server
echo ============================================================
echo.
echo   Cloud Logging: !CLOUD_VS_IP!:9999
echo   TCP Port:      2002
echo   VPN IP:        !VPN_IP!
echo.

cd /d "%PROJECT_ROOT%"
echo Starting...
"%MVN_PATH%" exec:java -pl aisafe.app.server -Dlogging.host=!CLOUD_VS_IP! -o

pause
