cmake_minimum_required(VERSION 3.11)
project(${PROJECT_NAME} C)

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/psp")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/ec1")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/stb/include")

set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

# Collect external C sources from gdx
#file(GLOB_RECURSE EXTERNAL_SOURCES_0 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx/*.c")
#list(APPEND SOURCES ${EXTERNAL_SOURCES_0})

# Collect external C++ sources from etc1
#file(GLOB_RECURSE EXTERNAL_SOURCES_1 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
#list(APPEND SOURCES ${EXTERNAL_SOURCES_1})

# Collect shared TeaVM native diagnostic C sources
set(TEAVM_MEMORY_STATS_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats/teavm_memory_stats.c")
if(EXISTS "${TEAVM_MEMORY_STATS_SOURCE}")
  list(APPEND SOURCES "${TEAVM_MEMORY_STATS_SOURCE}")
endif()

# Collect shared TeaVM optimization C sources
list(APPEND SOURCES
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_fastmath.c"
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_matrix4.c"
)
set(TEAVM_SPRITEBATCH_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_spritebatch.c")
set(TEAVM_SPRITEBATCH_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.h")
if(EXISTS "${TEAVM_SPRITEBATCH_SOURCE}" AND EXISTS "${TEAVM_SPRITEBATCH_HEADER}")
  list(APPEND SOURCES "${TEAVM_SPRITEBATCH_SOURCE}")
endif()

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
