cmake_minimum_required(VERSION 3.15)
project(${PROJECT_NAME} C)

set(CMAKE_C_STANDARD 11)

if(MSVC)
  # Packaged Windows native archives use the release static CRT in every configuration.
  set(CMAKE_MSVC_RUNTIME_LIBRARY "MultiThreaded")
endif()

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
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_fastmath.c"
        "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_matrix4.c"
)
set(TEAVM_GENERATED_SOURCES_LIST "${CMAKE_CURRENT_SOURCE_DIR}/c/src/all.txt")
set(TEAVM_SPRITEBATCH_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm/teavm_spritebatch.c")
set(TEAVM_SPRITEBATCH_LONG_SOURCE_PATH "classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.c")
set(TEAVM_SPRITEBATCH_SHORT_SOURCE_PATH "c/c/b/g/g/g/SpriteBatch.c")
set(TEAVM_SPRITEBATCH_LONG_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/classes/com/badlogic/gdx/graphics/g2d/SpriteBatch.h")
set(TEAVM_SPRITEBATCH_SHORT_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/c/c/b/g/g/g/SpriteBatch.h")
set(TEAVM_SPRITE_LONG_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/classes/com/badlogic/gdx/graphics/g2d/Sprite.h")
set(TEAVM_SPRITE_SHORT_HEADER "${CMAKE_CURRENT_SOURCE_DIR}/c/src/c/c/b/g/g/g/Sprite.h")
set(TEAVM_SPRITEBATCH_GENERATED FALSE)
set(TEAVM_SPRITEBATCH_SHORT_HEADERS FALSE)
set(TEAVM_SPRITE_GENERATED FALSE)
if(EXISTS "${TEAVM_GENERATED_SOURCES_LIST}")
  file(STRINGS "${TEAVM_GENERATED_SOURCES_LIST}" TEAVM_GENERATED_SOURCES)
  list(FIND TEAVM_GENERATED_SOURCES "${TEAVM_SPRITEBATCH_SHORT_SOURCE_PATH}" TEAVM_SPRITEBATCH_SHORT_INDEX)
  list(FIND TEAVM_GENERATED_SOURCES "${TEAVM_SPRITEBATCH_LONG_SOURCE_PATH}" TEAVM_SPRITEBATCH_LONG_INDEX)
  if(NOT TEAVM_SPRITEBATCH_SHORT_INDEX EQUAL -1)
    set(TEAVM_SPRITEBATCH_GENERATED TRUE)
    set(TEAVM_SPRITEBATCH_SHORT_HEADERS TRUE)
  elseif(NOT TEAVM_SPRITEBATCH_LONG_INDEX EQUAL -1)
    set(TEAVM_SPRITEBATCH_GENERATED TRUE)
  endif()
endif()
if(NOT TEAVM_SPRITEBATCH_GENERATED)
  if(EXISTS "${TEAVM_SPRITEBATCH_SHORT_HEADER}")
    set(TEAVM_SPRITEBATCH_GENERATED TRUE)
    set(TEAVM_SPRITEBATCH_SHORT_HEADERS TRUE)
  elseif(EXISTS "${TEAVM_SPRITEBATCH_LONG_HEADER}")
    set(TEAVM_SPRITEBATCH_GENERATED TRUE)
  endif()
endif()
if(EXISTS "${TEAVM_SPRITE_SHORT_HEADER}" OR EXISTS "${TEAVM_SPRITE_LONG_HEADER}")
  set(TEAVM_SPRITE_GENERATED TRUE)
endif()
if(EXISTS "${TEAVM_SPRITEBATCH_SOURCE}" AND TEAVM_SPRITEBATCH_GENERATED AND TEAVM_SPRITE_GENERATED)
  list(APPEND SOURCES "${TEAVM_SPRITEBATCH_SOURCE}")
endif()

set(TEAVM_EXTENSION_PRE_TARGET_DIR "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/cmake/pre_target")
if(EXISTS "${TEAVM_EXTENSION_PRE_TARGET_DIR}")
  file(GLOB TEAVM_EXTENSION_PRE_TARGET_FILES "${TEAVM_EXTENSION_PRE_TARGET_DIR}/*.cmake")
  list(SORT TEAVM_EXTENSION_PRE_TARGET_FILES)
  foreach(TEAVM_EXTENSION_PRE_TARGET_FILE IN LISTS TEAVM_EXTENSION_PRE_TARGET_FILES)
    include("${TEAVM_EXTENSION_PRE_TARGET_FILE}")
  endforeach()
endif()

# Collect external C++ sources from etc1
file(GLOB_RECURSE EXTERNAL_SOURCES_2 "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
list(APPEND SOURCES ${EXTERNAL_SOURCES_2})

# Add executable
set(TEAVM_APP_TARGET "${PROJECT_NAME}_app")
add_executable(${TEAVM_APP_TARGET} ${SOURCES})

if(MSVC)
  target_compile_definitions(${TEAVM_APP_TARGET} PRIVATE _ITERATOR_DEBUG_LEVEL=0)
endif()

if(TEAVM_SPRITEBATCH_SHORT_HEADERS)
  target_compile_definitions(${TEAVM_APP_TARGET} PRIVATE TEAVM_GENERATED_SHORT_FILE_NAMES=1)
endif()

set(TEAVM_EXTENSION_POST_TARGET_DIR "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/cmake/post_target")
if(EXISTS "${TEAVM_EXTENSION_POST_TARGET_DIR}")
  file(GLOB TEAVM_EXTENSION_POST_TARGET_FILES "${TEAVM_EXTENSION_POST_TARGET_DIR}/*.cmake")
  list(SORT TEAVM_EXTENSION_POST_TARGET_FILES)
  foreach(TEAVM_EXTENSION_POST_TARGET_FILE IN LISTS TEAVM_EXTENSION_POST_TARGET_FILES)
    include("${TEAVM_EXTENSION_POST_TARGET_FILE}")
  endforeach()
endif()

if (MSVC)
  target_compile_options(${TEAVM_APP_TARGET} PRIVATE
      $<$<CONFIG:Debug>:/Od /Zi>
      $<$<CONFIG:Release>:/O2 /Ob2 /Oi /Ot>)
elseif (CMAKE_C_COMPILER_ID MATCHES "GNU|Clang")
  target_compile_options(${TEAVM_APP_TARGET} PRIVATE
      $<$<CONFIG:Debug>:-g>
      $<$<CONFIG:Release>:-O3>)
endif()

# Set output name based on build configuration
set_target_properties(${TEAVM_APP_TARGET} PROPERTIES
    OUTPUT_NAME "${PROJECT_NAME}_$<IF:$<CONFIG:Debug>,debug,release>")

if (WIN32)
  # Set Visual Studio debugger working directory
  set_target_properties(${TEAVM_APP_TARGET} PROPERTIES
      VS_DEBUGGER_WORKING_DIRECTORY "${RELEASE_PATH}")
  # Link libraries
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE glfw3_mt opengl32 glew32s)

  # Set startup project for Visual Studio
  set_property(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} PROPERTY VS_STARTUP_PROJECT ${TEAVM_APP_TARGET})
endif()
if (UNIX)
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE OpenGL::GL glfw GLEW::GLEW m)
endif()

set_target_properties(${TEAVM_APP_TARGET} PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION_RELEASE ON
)
