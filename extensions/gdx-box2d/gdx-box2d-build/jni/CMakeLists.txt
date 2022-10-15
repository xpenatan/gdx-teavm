cmake_minimum_required(VERSION 3.13 FATAL_ERROR)
set(CMAKE_POLICY_DEFAULT_CMP0077 NEW)

FIND_PACKAGE(Python3)
set(PYTHON ${Python3_EXECUTABLE} CACHE STRING "Python path")
set(EMSCRIPTEN_ROOT $ENV{EMSDK}/upstream/emscripten CACHE STRING "Emscripten path")
set(CMAKE_TOOLCHAIN_FILE ${EMSCRIPTEN_ROOT}/cmake/Modules/Platform/Emscripten.cmake)
set(WEBIDL_BINDER_SCRIPT ${EMSCRIPTEN_ROOT}/tools/webidl_binder.py)
set(BOX2D_HEADER_FILE ${CMAKE_CURRENT_SOURCE_DIR}/basebox2D.h)
set(BOX2D_IDL_FILE ${CMAKE_CURRENT_SOURCE_DIR}/box2D.idl)
set(BOX2D_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/box2D/src)


set(BOX2D_TARGETS Collision Common Dynamics Rope)
foreach(_TARGET ${BOX2D_TARGETS})
  list(APPEND BOX2D_LIBS $<TARGET_FILE:${_TARGET}>)
endforeach()

# Disable box2D graphical benchmarks
set(USE_GRAPHICAL_BENCHMARK OFF)

# Disable box2D GLUT
set(USE_GLUT OFF)

# Build Release by default
set(CMAKE_BUILD_TYPE Release CACHE STRING "Build Type")

# Default is 64*1024*1024 = 64MB
set(TOTAL_MEMORY 67108864 CACHE STRING "Total Memory")

# Enable for resizable heap, with some amount of slowness
set(ALLOW_MEMORY_GROWTH 0 CACHE STRING "Allow Memory Growth")

set(EMCC_ARGS
  -O3
  -g0 -Wall
  --llvm-lto 1
  --post-js glue.js
  --closure 1
  -s ALLOW_MEMORY_GROWTH=${ALLOW_MEMORY_GROWTH}
  -s ALLOW_TABLE_GROWTH=1
  -s EXPORT_NAME="Box2DLib"
  -s MODULARIZE=1
  -s NO_EXIT_RUNTIME=1
  -s NO_FILESYSTEM=1
  -s TOTAL_MEMORY=${TOTAL_MEMORY}
  -s EXPORTED_FUNCTIONS=["_free","_malloc"]
  -s EXPORTED_RUNTIME_METHODS=["UTF8ToString"]

)

set(EMCC_JS_ARGS ${EMCC_ARGS}
  -s AGGRESSIVE_VARIABLE_ELIMINATION=1
  -s ELIMINATE_DUPLICATE_FUNCTIONS=1
  -s LEGACY_VM_SUPPORT=1
  -s SINGLE_FILE=1
  -s WASM=0
  )

set(EMCC_WASM_ARGS ${EMCC_ARGS}
  -s BINARYEN_IGNORE_IMPLICIT_TRAPS=1
  -s WASM=1
  )

set(EMCC_GLUE_ARGS
  -c
  -I${BOX2D_SRC_DIR}
  -include ../../custom_glue.cpp
  -include${BOX2D_HEADER_FILE}
  )


#######################################
project("box2D")
add_subdirectory(box2D EXCLUDE_FROM_ALL)


#######################################
add_custom_command(
  OUTPUT glue.cpp glue.js
  BYPRODUCTS parser.out WebIDLGrammar.pkl
  COMMAND ${PYTHON} ${WEBIDL_BINDER_SCRIPT} ${BOX2D_IDL_FILE} glue
  DEPENDS ${BOX2D_IDL_FILE}
  COMMENT "Generating box2D bindings"
  VERBATIM)
add_custom_command(
  OUTPUT glue.o
  COMMAND em++ glue.cpp ${EMCC_GLUE_ARGS} -o glue.o
  DEPENDS glue.cpp ${BOX2D_HEADER_FILE}
  COMMENT "Building box2D bindings"
  VERBATIM)
add_custom_target(box2D-bindings ALL DEPENDS glue.js glue.o)


#######################################
add_custom_command(
  OUTPUT box2D.js
  COMMAND em++ glue.o ${BOX2D_LIBS} ${EMCC_JS_ARGS} -o box2D.js
  DEPENDS box2D-bindings ${BOX2D_TARGETS}
  COMMENT "Building box2D javascript"
  VERBATIM)
add_custom_target(box2D-javascript ALL DEPENDS box2D.js)


#######################################
add_custom_command(
  OUTPUT box2D.wasm.js box2D.wasm.wasm
  COMMAND emcc glue.o ${BOX2D_LIBS} ${EMCC_WASM_ARGS} -o box2D.wasm.js
  DEPENDS box2D-bindings ${BOX2D_TARGETS}
  COMMENT "Building box2D webassembly"
  VERBATIM)
add_custom_target(box2D-wasm ALL DEPENDS box2D.wasm.js box2D.wasm.wasm)
