cmake_minimum_required(VERSION 3.11)
project(${PROJECT_NAME} C)

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/std/include")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/ec1")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx")

set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

# Collect external C sources from gdx
file(GLOB_RECURSE EXTERNAL_SOURCES_0 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx/*.c")
list(APPEND SOURCES ${EXTERNAL_SOURCES_0})

# Collect external C++ sources from etc1
file(GLOB_RECURSE EXTERNAL_SOURCES_1 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
list(APPEND SOURCES ${EXTERNAL_SOURCES_1})

add_executable(${PROJECT_NAME} ${SOURCES})

target_link_libraries(${PROJECT_NAME} PRIVATE
    pspgu      # Graphics Utility for 2D/3D rendering
    pspge      # Graphics Engine, low-level graphics operations
    pspgum     # Graphics Utility Matrix, for transformations and math
    pspvram    # Video RAM management
    pspdisplay # Display management and screen functions
    pspsdk     # PSP SDK core library
    pspdebug   # Debugging utilities
)

create_pbp_file(
    TARGET ${PROJECT_NAME}
    ICON_PATH NULL
    BACKGROUND_PATH NULL
    PREVIEW_PATH NULL
    TITLE ${PROJECT_NAME}
    VERSION 01.00
)