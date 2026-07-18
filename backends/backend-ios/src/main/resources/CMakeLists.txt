cmake_minimum_required(VERSION 3.18)
project(${PROJECT_NAME} C CXX)

set(CMAKE_C_STANDARD 11)
set(CMAKE_CXX_STANDARD 17)

# app_include.c lives in c/src and resolves generated quoted headers relative to
# itself. Do not add c/src globally: TeaVM emits headers such as string.h and
# uchar.h whose names would otherwise shadow the iOS SDK headers.
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/gdx")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/pure")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_optimizations/teavm")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats")
include_directories("${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/stb/include")

set(SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/src/app_include.c")

# app_include.c is a unity source that already includes gdx2d and the shared
# optimization bridges. Keep only native sources that it does not include here.
file(GLOB_RECURSE ETC1_SOURCES "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/etc1/etc1/*.cpp")
list(APPEND SOURCES ${ETC1_SOURCES})

set(TEAVM_MEMORY_STATS_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/teavm_stats/teavm_memory_stats.c")
if(EXISTS "${TEAVM_MEMORY_STATS_SOURCE}")
  list(APPEND SOURCES "${TEAVM_MEMORY_STATS_SOURCE}")
endif()

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
set(TEAVM_EXTENSION_PRE_TARGET_DIR "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/cmake/pre_target")
if(EXISTS "${TEAVM_EXTENSION_PRE_TARGET_DIR}")
  file(GLOB TEAVM_EXTENSION_PRE_TARGET_FILES "${TEAVM_EXTENSION_PRE_TARGET_DIR}/*.cmake")
  list(SORT TEAVM_EXTENSION_PRE_TARGET_FILES)
  foreach(TEAVM_EXTENSION_PRE_TARGET_FILE IN LISTS TEAVM_EXTENSION_PRE_TARGET_FILES)
    include("${TEAVM_EXTENSION_PRE_TARGET_FILE}")
  endforeach()
endif()

set(TEAVM_APP_TARGET "${PROJECT_NAME}")
add_library(${TEAVM_APP_TARGET} STATIC ${SOURCES})

if(CMAKE_C_COMPILER_ID MATCHES "Clang")
  target_compile_options(${TEAVM_APP_TARGET} PRIVATE
      -Wno-incompatible-pointer-types-discards-qualifiers
      -Wno-parentheses-equality
      -Wno-pointer-sign
      -Wno-unused-value)
endif()

if(TEAVM_SPRITEBATCH_SHORT_HEADERS)
  target_compile_definitions(${TEAVM_APP_TARGET} PRIVATE TEAVM_GENERATED_SHORT_FILE_NAMES=1)
endif()

set(TEAVM_IOS_STATIC_DEPENDENCY_TARGETS "")
set(TEAVM_EXTENSION_POST_TARGET_DIR "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/cmake/post_target")
if(EXISTS "${TEAVM_EXTENSION_POST_TARGET_DIR}")
  file(GLOB TEAVM_EXTENSION_POST_TARGET_FILES "${TEAVM_EXTENSION_POST_TARGET_DIR}/*.cmake")
  list(SORT TEAVM_EXTENSION_POST_TARGET_FILES)
  foreach(TEAVM_EXTENSION_POST_TARGET_FILE IN LISTS TEAVM_EXTENSION_POST_TARGET_FILES)
    include("${TEAVM_EXTENSION_POST_TARGET_FILE}")
  endforeach()
endif()

set(TEAVM_IOS_BUNDLE_ARCHIVE "${CMAKE_BINARY_DIR}/lib${PROJECT_NAME}_bundle.a")
set(TEAVM_IOS_BUNDLE_INPUTS "$<TARGET_FILE:${TEAVM_APP_TARGET}>")
set(TEAVM_IOS_BUNDLE_DEPENDS ${TEAVM_APP_TARGET})
foreach(TEAVM_IOS_DEPENDENCY_TARGET IN LISTS TEAVM_IOS_STATIC_DEPENDENCY_TARGETS)
  list(APPEND TEAVM_IOS_BUNDLE_INPUTS "$<TARGET_FILE:${TEAVM_IOS_DEPENDENCY_TARGET}>")
  list(APPEND TEAVM_IOS_BUNDLE_DEPENDS ${TEAVM_IOS_DEPENDENCY_TARGET})
endforeach()

add_custom_command(
  OUTPUT "${TEAVM_IOS_BUNDLE_ARCHIVE}"
  COMMAND "${CMAKE_COMMAND}" -E rm -f "${TEAVM_IOS_BUNDLE_ARCHIVE}"
  COMMAND /usr/bin/libtool -static -o "${TEAVM_IOS_BUNDLE_ARCHIVE}" ${TEAVM_IOS_BUNDLE_INPUTS}
  DEPENDS ${TEAVM_IOS_BUNDLE_DEPENDS}
  VERBATIM)
add_custom_target("${PROJECT_NAME}_bundle" ALL DEPENDS "${TEAVM_IOS_BUNDLE_ARCHIVE}")
