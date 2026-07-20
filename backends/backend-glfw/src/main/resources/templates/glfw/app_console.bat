@echo off
title ${WINDOW_TITLE}

cd /d "${WORKING_DIRECTORY}"
if errorlevel 1 (
    echo Failed to change directory to ${WORKING_DIRECTORY}
    exit /b 1
)

"${EXECUTABLE_PATH}"
set "APP_EXIT_CODE=%ERRORLEVEL%"

echo.
if "%APP_EXIT_CODE%"=="0" (
    echo Application closed normally.
) else (
    echo Application terminated with exit code %APP_EXIT_CODE%.
)

echo Press any key to close this console...
pause >nul
exit /b %APP_EXIT_CODE%
