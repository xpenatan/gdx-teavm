if(WIN32)
  target_include_directories(${TEAVM_APP_TARGET} PRIVATE
      "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/websockets")
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE winhttp)
endif()

if(UNIX)
  find_package(Threads REQUIRED)
  target_include_directories(${TEAVM_APP_TARGET} PRIVATE
      "${CMAKE_CURRENT_SOURCE_DIR}/c/external_cpp/websockets")
  target_link_libraries(${TEAVM_APP_TARGET} PRIVATE Threads::Threads ${CMAKE_DL_LIBS})
endif()
