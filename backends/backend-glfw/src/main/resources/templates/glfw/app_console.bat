@echo off
title ${WINDOW_TITLE}

cd /d "${WORKING_DIRECTORY}"
if errorlevel 1 (
    echo Failed to change directory to ${WORKING_DIRECTORY}
    exit /b 1
)

"${EXECUTABLE_PATH}"
exit /b %ERRORLEVEL%
