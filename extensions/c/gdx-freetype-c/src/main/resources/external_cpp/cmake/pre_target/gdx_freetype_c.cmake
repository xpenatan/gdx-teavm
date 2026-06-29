set(TEAVM_FREETYPE_SOURCE "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/freetype/teavm_freetype.c")
set(TEAVM_FREETYPE_GENERATED FALSE)
set(TEAVM_FREETYPE_ENABLED FALSE)

if(EXISTS "${TEAVM_GENERATED_SOURCES_LIST}")
  if(NOT DEFINED TEAVM_GENERATED_SOURCES)
    file(STRINGS "${TEAVM_GENERATED_SOURCES_LIST}" TEAVM_GENERATED_SOURCES)
  endif()
  foreach(TEAVM_GENERATED_SOURCE IN LISTS TEAVM_GENERATED_SOURCES)
    if(TEAVM_GENERATED_SOURCE MATCHES "graphics/g2d/freetype/FreeType\\.c$")
      set(TEAVM_FREETYPE_GENERATED TRUE)
    endif()
  endforeach()
endif()

if(NOT TEAVM_FREETYPE_GENERATED)
  file(GLOB_RECURSE TEAVM_FREETYPE_HEADERS "${CMAKE_CURRENT_SOURCE_DIR}/c/src/*FreeType.h")
  if(TEAVM_FREETYPE_HEADERS)
    set(TEAVM_FREETYPE_GENERATED TRUE)
  endif()
endif()

if(EXISTS "${TEAVM_FREETYPE_SOURCE}" AND TEAVM_FREETYPE_GENERATED)
  include(FetchContent)
  set(FT_DISABLE_ZLIB ON CACHE BOOL "" FORCE)
  set(FT_DISABLE_BZIP2 ON CACHE BOOL "" FORCE)
  set(FT_DISABLE_PNG ON CACHE BOOL "" FORCE)
  set(FT_DISABLE_HARFBUZZ ON CACHE BOOL "" FORCE)
  set(FT_DISABLE_BROTLI ON CACHE BOOL "" FORCE)
  FetchContent_Declare(teavm_freetype
    URL
      https://download-mirror.savannah.gnu.org/releases/freetype/freetype-2.14.3.tar.xz
      https://download.savannah.gnu.org/releases/freetype/freetype-2.14.3.tar.xz
      https://sourceforge.net/projects/freetype/files/freetype2/2.14.3/freetype-2.14.3.tar.xz/download
    URL_HASH SHA256=36bc4f1cc413335368ee656c42afca65c5a3987e8768cc28cf11ba775e785a5f
    DOWNLOAD_EXTRACT_TIMESTAMP TRUE)
  FetchContent_MakeAvailable(teavm_freetype)
  list(APPEND SOURCES "${TEAVM_FREETYPE_SOURCE}")
  set(TEAVM_FREETYPE_ENABLED TRUE)
endif()
