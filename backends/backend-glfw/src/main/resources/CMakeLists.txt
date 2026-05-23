cmake_minimum_required(VERSION 3.10)
project(${PROJECT_NAME} C)

set(CMAKE_C_STANDARD 11)

# Output directory configuration
if(CMAKE_CONFIGURATION_TYPES)
    foreach(config ${CMAKE_CONFIGURATION_TYPES})
        string(TOUPPER ${config} config_upper)
        set(CMAKE_RUNTIME_OUTPUT_DIRECTORY_${config_upper} "${RELEASE_PATH}")
    endforeach()
else()
    set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${RELEASE_PATH}")
endif()

# Default to Release build if not specified
if(NOT CMAKE_BUILD_TYPE)
    set(CMAKE_BUILD_TYPE Release)
endif()

if (UNIX)
  find_package(glfw3 CONFIG REQUIRED)
  find_package(OpenGL REQUIRED)
  find_package(GLEW REQUIRED)
endif()

if (WIN32)
  # Define GLEW_STATIC
  add_definitions(-DGLEW_STATIC)
endif()

if (WIN32)
# Include directories - using CMAKE_CURRENT_SOURCE_DIR
  include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/glfw/include")
  include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/glew-2.3.0/include")
endif()

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/stb/include")

if (WIN32)
  # Link directories
  link_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/glfw/lib-vc2022")
  link_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/glew-2.3.0/lib/Release/x64")
endif()

# Source files
set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

# Collect external C sources from gdx
file(GLOB_RECURSE EXTERNAL_SOURCES_0 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx/*.c")
list(APPEND SOURCES ${EXTERNAL_SOURCES_0})

# Collect shared TeaVM native diagnostic C sources
set(TEAVM_MEMORY_STATS_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats/teavm_memory_stats.c")
if(EXISTS "${TEAVM_MEMORY_STATS_SOURCE}")
  list(APPEND SOURCES "${TEAVM_MEMORY_STATS_SOURCE}")
endif()

# Collect shared TeaVM optimization C sources
list(APPEND SOURCES
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure/matrix4.c"
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure/spritebatch.c"
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_matrix4.c"
)
set(TEAVM_SPRITEBATCH_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_spritebatch.c")
set(TEAVM_SPRITEBATCH_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.h")
if(EXISTS "${TEAVM_SPRITEBATCH_SOURCE}" AND EXISTS "${TEAVM_SPRITEBATCH_HEADER}")
  list(APPEND SOURCES "${TEAVM_SPRITEBATCH_SOURCE}")
endif()

# Collect external C++ sources from etc1
file(GLOB_RECURSE EXTERNAL_SOURCES_2 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
list(APPEND SOURCES ${EXTERNAL_SOURCES_2})

# Add executable
add_executable(${PROJECT_NAME} ${SOURCES})

if (MSVC)
  target_compile_options(${PROJECT_NAME} PRIVATE
      $<$<CONFIG:Debug>:/Od /Zi>
      $<$<CONFIG:Release>:/O2 /Ob2 /Oi /Ot>)
elseif (CMAKE_C_COMPILER_ID MATCHES "GNU|Clang")
  target_compile_options(${PROJECT_NAME} PRIVATE
      $<$<CONFIG:Debug>:-g>
      $<$<CONFIG:Release>:-O3>)
endif()

# Set output name based on build configuration
set_target_properties(${PROJECT_NAME} PROPERTIES
    OUTPUT_NAME "${PROJECT_NAME}_$<IF:$<CONFIG:Debug>,debug,release>")

if (WIN32)
  # Set Visual Studio debugger working directory
  set_target_properties(${PROJECT_NAME} PROPERTIES
      VS_DEBUGGER_WORKING_DIRECTORY "${RELEASE_PATH}")
  # Link libraries
  target_link_libraries(${PROJECT_NAME} glfw3 opengl32 glew32s)

  # Set startup project for Visual Studio
  set_property(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} PROPERTY VS_STARTUP_PROJECT ${PROJECT_NAME})
endif()
if (UNIX)
  target_link_libraries(${PROJECT_NAME} PRIVATE OpenGL::GL glfw GLEW::GLEW m)
endif()

set_target_properties(${PROJECT_NAME} PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION_RELEASE ON
)
