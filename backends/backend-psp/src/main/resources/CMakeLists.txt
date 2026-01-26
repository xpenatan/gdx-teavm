cmake_minimum_required(VERSION 3.11)
project(${PROJECT_NAME} C)

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gu2gl")

# Source files
set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

add_executable(${PROJECT_NAME} ${SOURCES})

target_link_libraries(${PROJECT_NAME} PRIVATE
    pspgu
    pspge
    pspgum
    pspvram
    pspdisplay
    psphprm
    pspsdk
    pspdebug
)

# Create an EBOOT.PBP file
create_pbp_file(
    TARGET ${PROJECT_NAME}
    ICON_PATH NULL
    BACKGROUND_PATH NULL
    PREVIEW_PATH NULL
    TITLE ${PROJECT_NAME}
    VERSION 01.00
)