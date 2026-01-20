@echo off
setlocal enabledelayedexpansion

:: Modifiable Names
set NAME=gdx-c

:: Use CMake to create build files
cmake -S . -B build\cmake
if errorlevel 1 (
    echo CMake build generation failed
    exit /b 1
)

echo Changing directory to build\cmake...
cd build\cmake
if errorlevel 1 (
    echo Failed to change directory
    exit /b 1
)
echo Current directory: %CD%

:: Initialize MSBuild path variable
set MSBUILD_PATH=
set VS_EDITION=

:: Search Visual Studio installations (two levels deep)
for %%R in ("%ProgramFiles%" "%ProgramFiles(x86)%") do (
    for /d %%Y in ("%%~R\Microsoft Visual Studio\*") do (
        for /d %%E in ("%%~Y\*") do (
            set "EDITION_DIR=%%~nxE"
            set "CHECK_PATH=%%~fE\MSBuild\Current\Bin"

            if exist "!CHECK_PATH!\MSBuild.exe" (
                set "MSBUILD_PATH=!CHECK_PATH!\MSBuild.exe"
                set "VS_EDITION=!EDITION_DIR!"
                goto :found
            )
            if exist "!CHECK_PATH!\amd64\MSBuild.exe" (
                set "MSBUILD_PATH=!CHECK_PATH!\amd64\MSBuild.exe"
                set "VS_EDITION=!EDITION_DIR!"
                goto :found
            )
            if exist "!CHECK_PATH!\x86\MSBuild.exe" (
                set "MSBUILD_PATH=!CHECK_PATH!\x86\MSBuild.exe"
                set "VS_EDITION=!EDITION_DIR!"
                goto :found
            )
        )
    )
)

:: If MSBuild path is not found, check in the registry for installations
echo MSBuild not found in default paths. Checking registry for installation...

for /f "tokens=2* delims=    " %%A in ('reg query "HKCU\Software\Microsoft\VisualStudio" /s /f "MSBuild.exe" 2^>nul') do (
    if "%%B" neq "" (
        set MSBUILD_PATH=%%B
        set VS_EDITION=Unknown
        goto :found
    )
)

:: If MSBuild is still not found, display an error
echo Error: MSBuild not found on your system.
exit /b 1

:found
:: Display the found MSBuild path and edition
echo MSBuild found at: %MSBUILD_PATH%
echo Visual Studio Edition Detected: %VS_EDITION%

if /i "%VS_EDITION%"=="BuildTools" (
    echo Error: Visual Studio Build Tools edition is not supported.
    exit /b 1
)

:: Set the solution/project file you want to build
set SOLUTION_FILE=".\%NAME%.slnx"

:: Run MSBuild
echo Running MSBuild...
"%MSBUILD_PATH%" %SOLUTION_FILE%

if errorlevel 1 (
    echo Failed to build solution
    exit /b 1
)

endlocal
