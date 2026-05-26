cmake_minimum_required(VERSION 3.10)
project(${PROJECT_NAME} C CXX)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)

include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/stb/include")

set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

file(GLOB_RECURSE GDX_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx/*.c")
list(APPEND SOURCES ${GDX_SOURCES})

file(GLOB_RECURSE ETC1_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
list(APPEND SOURCES ${ETC1_SOURCES})

set(TEAVM_MEMORY_STATS_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats/teavm_memory_stats.c")
if(EXISTS "${TEAVM_MEMORY_STATS_SOURCE}")
  list(APPEND SOURCES "${TEAVM_MEMORY_STATS_SOURCE}")
endif()

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
set(TEAVM_SPRITEBATCH_GENERATED FALSE)
set(TEAVM_SPRITEBATCH_SHORT_HEADERS FALSE)
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
if(EXISTS "${TEAVM_SPRITEBATCH_SOURCE}" AND TEAVM_SPRITEBATCH_GENERATED)
  list(APPEND SOURCES "${TEAVM_SPRITEBATCH_SOURCE}")
endif()

add_library(${PROJECT_NAME} SHARED ${SOURCES})
set_target_properties(${PROJECT_NAME} PROPERTIES LINK_FLAGS "-Wl,-z,max-page-size=16384")

if(CMAKE_C_COMPILER_ID MATCHES "Clang")
  target_compile_options(${PROJECT_NAME} PRIVATE
      -Wno-incompatible-pointer-types-discards-qualifiers
      -Wno-parentheses-equality
      -Wno-pointer-sign
      -Wno-unused-value)
endif()

if(TEAVM_SPRITEBATCH_SHORT_HEADERS)
  target_compile_definitions(${PROJECT_NAME} PRIVATE TEAVM_GENERATED_SHORT_FILE_NAMES=1)
endif()

target_link_libraries(${PROJECT_NAME} android log GLESv3)
