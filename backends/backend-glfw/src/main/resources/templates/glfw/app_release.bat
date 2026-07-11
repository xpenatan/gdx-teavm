@echo off
setlocal enabledelayedexpansion

:: Build ${BUILD_CONFIG} Configuration
set "NAME=${PROJECT_NAME}"
set "BUILD_CONFIG=${BUILD_CONFIG}"

:: Change to the output directory where CMakeLists.txt is located
cd /d "%~dp0"
if errorlevel 1 (
    echo Failed to change directory
    exit /b 1
)

:: Use CMake to create build files
echo Generating CMake build files for !BUILD_CONFIG!...

:: Preserve literal exclamation marks in discovered paths and user-provided CMake definition values.
setlocal disabledelayedexpansion

:: Try CMake from PATH first.
set "CMAKE_PATH=cmake.exe"
where.exe cmake.exe >nul 2>&1
if errorlevel 1 set "CMAKE_PATH="

:: Ask Visual Studio Installer for the newest installation containing its CMake component.
if not defined CMAKE_PATH (
    echo cmake not found in PATH, trying Visual Studio Installer...
    set "VSWHERE_PATH=%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe"
)
if not defined CMAKE_PATH if not exist "%VSWHERE_PATH%" (
    set "VSWHERE_PATH=%ProgramFiles%\Microsoft Visual Studio\Installer\vswhere.exe"
)
if not defined CMAKE_PATH if exist "%VSWHERE_PATH%" (
    for /f "usebackq delims=" %%I in (`"%VSWHERE_PATH%" -latest -products * -requires Microsoft.VisualStudio.Component.VC.CMake.Project -find Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin\cmake.exe 2^>nul`) do (
        if not defined CMAKE_PATH if exist "%%~I" set "CMAKE_PATH=%%~I"
    )
)

:: Fallback for installations where the Visual Studio Installer locator is unavailable.
if not defined CMAKE_PATH (
    echo Visual Studio CMake not found, trying common install locations...
    for %%D in (C D E) do (
        for %%P in ("Program Files" "Program Files (x86)") do (
            for %%V in (18 2022 2019 2017) do (
                for %%S in (Community Professional Enterprise BuildTools) do (
                    if not defined CMAKE_PATH if exist "%%D:\%%~P\Microsoft Visual Studio\%%V\%%S\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin\cmake.exe" set "CMAKE_PATH=%%D:\%%~P\Microsoft Visual Studio\%%V\%%S\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin\cmake.exe"
                )
            )
            if not defined CMAKE_PATH if exist "%%D:\%%~P\CMake\bin\cmake.exe" set "CMAKE_PATH=%%D:\%%~P\CMake\bin\cmake.exe"
        )
    )
)

if not defined CMAKE_PATH (
    echo cmake could not be found. Please install CMake or add it to PATH.
    exit /b 1
)

"%CMAKE_PATH%" -S . -B build\cmake${CMAKE_DEFINITIONS}
if errorlevel 1 (
    echo CMake build generation failed
    exit /b 1
)

echo Building %BUILD_CONFIG% configuration...
"%CMAKE_PATH%" --build build\cmake --config "%BUILD_CONFIG%"
if errorlevel 1 (
    echo Failed to build CMake project
    exit /b 1
)

echo Build completed successfully for %BUILD_CONFIG% configuration.
endlocal
endlocal
