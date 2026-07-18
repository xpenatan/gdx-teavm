if(TEAVM_FREETYPE_ENABLED)
  target_include_directories(${TEAVM_APP_TARGET} PRIVATE
      "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/freetype"
      "${teavm_freetype_SOURCE_DIR}/include")
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE freetype)
  if(DEFINED TEAVM_IOS_STATIC_DEPENDENCY_TARGETS)
    list(APPEND TEAVM_IOS_STATIC_DEPENDENCY_TARGETS freetype)
  endif()
endif()
