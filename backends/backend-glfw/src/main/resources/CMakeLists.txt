cmake_minimum_required(VERSION 3.15)
project(${PROJECT_NAME} C)

set(CMAKE_C_STANDARD 11)

# Keep the existing MT behavior as the default, but preserve an explicit
# standard CMake runtime selection supplied by the consumer.
if(MSVC AND (NOT DEFINED CMAKE_MSVC_RUNTIME_LIBRARY
    OR "${CMAKE_MSVC_RUNTIME_LIBRARY}" STREQUAL ""))
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

option(GDX_TEAVM_GLFW_USE_SYSTEM_LIBS
    "Use GLFW and GLEW installed by the Unix host instead of pinned static sources"
    OFF)

if(UNIX)
  find_package(OpenGL REQUIRED)

  if(GDX_TEAVM_GLFW_USE_SYSTEM_LIBS)
    find_package(glfw3 3.4 CONFIG REQUIRED)
    find_package(GLEW REQUIRED)
    set(TEAVM_GLFW_LIBRARY glfw)
    set(TEAVM_GLEW_LIBRARY GLEW::GLEW)
  else()
    include(FetchContent)

    if(NOT DEFINED GLFW_LIBRARY_TYPE)
      set(GLFW_LIBRARY_TYPE STATIC CACHE STRING "GLFW library type")
    endif()
    foreach(GLFW_OPTION GLFW_BUILD_EXAMPLES GLFW_BUILD_TESTS GLFW_BUILD_DOCS GLFW_INSTALL)
      if(NOT DEFINED ${GLFW_OPTION})
        set(${GLFW_OPTION} OFF CACHE BOOL "Disabled for the embedded GLFW build")
      endif()
    endforeach()

    FetchContent_Declare(gdx_teavm_glfw
        URL "https://github.com/glfw/glfw/releases/download/3.4/glfw-3.4.zip"
        URL_HASH "SHA256=b5ec004b2712fd08e8861dc271428f048775200a2df719ccf575143ba749a3e9")
    FetchContent_MakeAvailable(gdx_teavm_glfw)

    FetchContent_Declare(gdx_teavm_glew_source
        URL "https://github.com/nigels-com/glew/releases/download/glew-2.3.0/glew-2.3.0.zip"
        URL_HASH "SHA256=fe8fdbaa77cfa354ff400da323ea5e32b3641ad58a218607de74d2998b872e66")
    FetchContent_GetProperties(gdx_teavm_glew_source)
    if(NOT gdx_teavm_glew_source_POPULATED)
      FetchContent_Populate(gdx_teavm_glew_source)
    endif()

    add_library(gdx_teavm_glew STATIC
        "${gdx_teavm_glew_source_SOURCE_DIR}/src/glew.c")
    target_include_directories(gdx_teavm_glew PUBLIC
        "${gdx_teavm_glew_source_SOURCE_DIR}/include")
    target_compile_definitions(gdx_teavm_glew PUBLIC GLEW_STATIC GLEW_NO_GLU)
    target_link_libraries(gdx_teavm_glew PUBLIC OpenGL::GL)
    if(NOT APPLE)
      find_package(X11 REQUIRED)
      target_include_directories(gdx_teavm_glew PRIVATE ${X11_INCLUDE_DIR})
      target_link_libraries(gdx_teavm_glew PUBLIC ${X11_LIBRARIES})
      if(TARGET OpenGL::GLX)
        target_link_libraries(gdx_teavm_glew PUBLIC OpenGL::GLX)
      endif()
    endif()
    if(CMAKE_DL_LIBS)
      target_link_libraries(gdx_teavm_glew PUBLIC ${CMAKE_DL_LIBS})
    endif()

    set(TEAVM_GLFW_LIBRARY glfw)
    set(TEAVM_GLEW_LIBRARY gdx_teavm_glew)
  endif()
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

if(UNIX AND NOT APPLE)
  if(GDX_TEAVM_GLFW_USE_SYSTEM_LIBS)
    option(GDX_TEAVM_GLFW_NATIVE_X11
        "The system GLFW library provides X11 native access" ON)
    option(GDX_TEAVM_GLFW_NATIVE_WAYLAND
        "The system GLFW library provides Wayland native access" ON)
  else()
    set(GDX_TEAVM_GLFW_NATIVE_X11 ${GLFW_BUILD_X11})
    set(GDX_TEAVM_GLFW_NATIVE_WAYLAND ${GLFW_BUILD_WAYLAND})
  endif()

  if(GDX_TEAVM_GLFW_NATIVE_X11)
    target_compile_definitions(${TEAVM_APP_TARGET} PRIVATE GDX_TEAVM_GLFW_NATIVE_X11=1)
  endif()
  if(GDX_TEAVM_GLFW_NATIVE_WAYLAND)
    target_compile_definitions(${TEAVM_APP_TARGET} PRIVATE GDX_TEAVM_GLFW_NATIVE_WAYLAND=1)
  endif()
endif()

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
  if(MSVC)
    get_target_property(TEAVM_MSVC_RUNTIME_LIBRARY
        ${TEAVM_APP_TARGET} MSVC_RUNTIME_LIBRARY)
    if(NOT TEAVM_MSVC_RUNTIME_LIBRARY
        OR TEAVM_MSVC_RUNTIME_LIBRARY MATCHES "-NOTFOUND$")
      set(TEAVM_MSVC_RUNTIME_LIBRARY "${CMAKE_MSVC_RUNTIME_LIBRARY}")
    endif()
    string(TOUPPER "${TEAVM_MSVC_RUNTIME_LIBRARY}" TEAVM_MSVC_RUNTIME_LIBRARY)
    if(TEAVM_MSVC_RUNTIME_LIBRARY MATCHES "DLL")
      set(TEAVM_GLFW_LIBRARY glfw3)
      set(TEAVM_GLEW_LIBRARY glew32s_md)
    else()
      set(TEAVM_GLFW_LIBRARY glfw3_mt)
      set(TEAVM_GLEW_LIBRARY glew32s)
    endif()
  else()
    set(TEAVM_GLFW_LIBRARY glfw3_mt)
    set(TEAVM_GLEW_LIBRARY glew32s)
  endif()
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE
      ${TEAVM_GLFW_LIBRARY}
      opengl32
      ${TEAVM_GLEW_LIBRARY})

  # Set startup project for Visual Studio
  set_property(DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR} PROPERTY VS_STARTUP_PROJECT ${TEAVM_APP_TARGET})
endif()
if(UNIX)
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE
      ${TEAVM_GLFW_LIBRARY}
      ${TEAVM_GLEW_LIBRARY}
      OpenGL::GL
      m)
endif()

set_target_properties(${TEAVM_APP_TARGET} PROPERTIES
    INTERPROCEDURAL_OPTIMIZATION_RELEASE ON
)
