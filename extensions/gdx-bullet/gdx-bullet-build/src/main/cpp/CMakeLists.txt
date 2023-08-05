cmake_minimum_required(VERSION 3.13 FATAL_ERROR)
set(CMAKE_POLICY_DEFAULT_CMP0077 NEW)

FIND_PACKAGE(Python3)
set(PYTHON ${Python3_EXECUTABLE} CACHE STRING "Python path")
set(EMSCRIPTEN_ROOT $ENV{EMSDK}/upstream/emscripten CACHE STRING "Emscripten path")
set(CMAKE_TOOLCHAIN_FILE ${EMSCRIPTEN_ROOT}/cmake/Modules/Platform/Emscripten.cmake)
set(WEBIDL_BINDER_SCRIPT ${EMSCRIPTEN_ROOT}/tools/webidl_binder.py)
set(BULLET_HEADER_FILE ${CMAKE_CURRENT_SOURCE_DIR}/bullet.h)
set(BULLET_IDL_FILE ${CMAKE_CURRENT_SOURCE_DIR}/bullet.idl)
set(BULLET_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/bullet/src)


set(BULLET_TARGETS BulletCollision BulletDynamics BulletSoftBody LinearMath)
foreach(_TARGET ${BULLET_TARGETS})
  list(APPEND BULLET_LIBS $<TARGET_FILE:${_TARGET}>)
endforeach()

# Disable bullet graphical benchmarks
set(USE_GRAPHICAL_BENCHMARK OFF)

# Disable bullet GLUT
set(USE_GLUT OFF)

# Build Release by default
set(CMAKE_BUILD_TYPE Release CACHE STRING "Build Type")

# Default is 64*1024*1024 = 64MB
set(INITIAL_MEMORY 67108864 CACHE STRING "Total Memory")

# Enable for resizable heap, with some amount of slowness
set(ALLOW_MEMORY_GROWTH 0 CACHE STRING "Allow Memory Growth")

set(EMCC_ARGS
  -O3
  -g0 -Wall
  --llvm-lto 1
  --post-js glue.js
  --extern-post-js ../../src/main/cpp/post.js
  --closure 1
  -s ALLOW_MEMORY_GROWTH=${ALLOW_MEMORY_GROWTH}
  -s ALLOW_TABLE_GROWTH=1
  -s EXPORT_NAME="Bullet"
  -s MODULARIZE=1
  -s NO_FILESYSTEM=1
  -s INITIAL_MEMORY=${INITIAL_MEMORY}
  -s EXPORTED_FUNCTIONS=["_free","_malloc"]
  -s EXPORTED_RUNTIME_METHODS=["UTF8ToString"]
)

#set(EMCC_JS_ARGS ${EMCC_ARGS}
#  -s AGGRESSIVE_VARIABLE_ELIMINATION=1
#  -s ELIMINATE_DUPLICATE_FUNCTIONS=1
#  -s SINGLE_FILE=1
#  -s WASM=0
#)

set(EMCC_WASM_ARGS ${EMCC_ARGS}
  -s BINARYEN_IGNORE_IMPLICIT_TRAPS=1
  -s WASM=1
  -s SINGLE_FILE=1
)

set(EMCC_GLUE_ARGS
  -c
  -I${BULLET_SRC_DIR}
  -include ../../custom_glue.cpp
  -include${BULLET_HEADER_FILE}
)

#######################################
project("bullet")
add_subdirectory(bullet EXCLUDE_FROM_ALL)

#######################################
add_custom_command(
  OUTPUT glue.cpp glue.js
  BYPRODUCTS parser.out WebIDLGrammar.pkl
  COMMAND ${PYTHON} ${WEBIDL_BINDER_SCRIPT} ${BULLET_IDL_FILE} glue
  DEPENDS ${BULLET_IDL_FILE}
  COMMENT "Generating bullet bindings"
  VERBATIM
)
add_custom_command(
  OUTPUT glue.o
  COMMAND em++ glue.cpp ${EMCC_GLUE_ARGS} -o glue.o
  DEPENDS glue.cpp ${BULLET_HEADER_FILE}
  COMMENT "Building bullet bindings"
  VERBATIM
)
add_custom_target(bullet-bindings ALL DEPENDS glue.js glue.o)

#######################################
#add_custom_command(
#  OUTPUT bullet.js
#  COMMAND em++ glue.o ${BULLET_LIBS} ${EMCC_JS_ARGS} -o bullet.js
#  DEPENDS bullet-bindings ${BULLET_TARGETS}
#  COMMENT "Building bullet javascript"
#  VERBATIM)
#add_custom_target(bullet-javascript ALL DEPENDS bullet.js)

#######################################
add_custom_command(
  OUTPUT bullet.wasm.js bullet.wasm.wasm
  COMMAND emcc glue.o ${BULLET_LIBS} ${EMCC_WASM_ARGS} -o bullet.wasm.js
  DEPENDS bullet-bindings ${BULLET_TARGETS}
  COMMENT "Building bullet webassembly"
  VERBATIM
)
add_custom_target(bullet-wasm ALL DEPENDS bullet.wasm.js bullet.wasm.wasm)
