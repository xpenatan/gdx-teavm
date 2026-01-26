@echo off
setlocal

REM ====================================
REM  Fast PSP EBOOT Builder (WSL)
REM ====================================

echo.
echo ====================================
echo   Building PSP EBOOT via WSL...
echo ====================================
echo.

REM Check if WSL is available
wsl --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: WSL not found!
    echo Please install WSL and PSP toolchain.
    pause
    exit /b 1
)

REM Get Windows path and convert to WSL path
set "WINPATH=%CD%"
for /f "tokens=*" %%i in ('wsl wslpath -u "%WINPATH%"') do set "WSLPATH=%%i"

echo Working directory: %WSLPATH%
echo.

REM Run the build script in WSL
wsl bash -c "cd '%WSLPATH%' && bash build.sh"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Build failed!
    exit /b 1
)

echo.
echo ====================================
echo   SUCCESS!
echo ====================================
echo.
echo EBOOT.PBP created successfully!
echo Location: %CD%\c\release\EBOOT.PBP
echo.
