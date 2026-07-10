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

:: Try to find cmake
set CMAKE_PATH=cmake
where cmake >nul 2>&1
if errorlevel 1 (
    echo cmake not found in PATH, scanning common Visual Studio locations...
    set "CMAKE_PATH="
    for %%D in (C D E) do (
        for %%P in ("Program Files" "Program Files (x86)") do (
            if not defined CMAKE_PATH (
                set "CANDIDATE=%%D:\%%~P\Microsoft Visual Studio\18\Community\Common7\IDE\CommonExtensions\Microsoft\CMake\CMake\bin\cmake.exe"
                if exist "!CANDIDATE!" (
                    set "CMAKE_PATH=!CANDIDATE!"
                )
            )
        )
    )
    if not defined CMAKE_PATH (
        echo cmake could not be found. Please install CMake or add it to PATH.
        exit /b 1
    )
)

"!CMAKE_PATH!" -S . -B build\cmake
if errorlevel 1 (
    echo CMake build generation failed
    exit /b 1
)

echo Building !BUILD_CONFIG! configuration...
"!CMAKE_PATH!" --build build\cmake --config !BUILD_CONFIG!
if errorlevel 1 (
    echo Failed to build CMake project
    exit /b 1
)

echo Build completed successfully for !BUILD_CONFIG! configuration.
endlocal
