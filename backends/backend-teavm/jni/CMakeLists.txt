cmake_minimum_required(VERSION 3.13 FATAL_ERROR)
set(CMAKE_POLICY_DEFAULT_CMP0077 NEW)

FIND_PACKAGE(Python3)
set(PYTHON ${Python3_EXECUTABLE} CACHE STRING "Python path")
set(EMSCRIPTEN_ROOT $ENV{EMSDK}/upstream/emscripten CACHE STRING "Emscripten path")
set(CMAKE_TOOLCHAIN_FILE ${EMSCRIPTEN_ROOT}/cmake/Modules/Platform/Emscripten.cmake)
set(WEBIDL_BINDER_SCRIPT ${EMSCRIPTEN_ROOT}/tools/webidl_binder.py)
set(GDX_HEADER_FILE ${CMAKE_CURRENT_SOURCE_DIR}/basegdx.h)
set(GDX_IDL_FILE ${CMAKE_CURRENT_SOURCE_DIR}/gdx.idl)
set(GDX_SRC_DIR ${CMAKE_CURRENT_SOURCE_DIR}/gdx/src)

set(GDX_TARGETS Gdx2D)
foreach(_TARGET ${GDX_TARGETS})
  list(APPEND GDX_LIBS $<TARGET_FILE:${_TARGET}>)
endforeach()

# Disable gdx graphical benchmarks
set(USE_GRAPHICAL_BENCHMARK OFF)

# Disable gdx GLUT
set(USE_GLUT OFF)

# Build Release by default
set(CMAKE_BUILD_TYPE Release CACHE STRING "Build Type")

# Default is 64*1024*1024 = 64MB
set(INITIAL_MEMORY 67108864 CACHE STRING "Total Memory")

# Enable for resizable heap, with some amount of slowness
set(ALLOW_MEMORY_GROWTH 1 CACHE STRING "Allow Memory Growth")

set(EMCC_ARGS
  -O3
  -g0 -Wall
  --llvm-lto 1
  --post-js glue.js
  --extern-post-js ../../post.js
  --closure 1
  -s ALLOW_MEMORY_GROWTH=${ALLOW_MEMORY_GROWTH}
  -s ALLOW_TABLE_GROWTH=1
  -s EXPORT_NAME="Gdx"
  -s MODULARIZE=1
  -s NO_FILESYSTEM=1
  -s INITIAL_MEMORY=${INITIAL_MEMORY}
  -s EXPORTED_FUNCTIONS=_free,_malloc
  -s EXPORTED_RUNTIME_METHODS=UTF8ToString,writeArrayToMemory
)

#set(EMCC_JS_ARGS ${EMCC_ARGS}
#  -s AGGRESSIVE_VARIABLE_ELIMINATION=1
#  -s ELIMINATE_DUPLICATE_FUNCTIONS=1
#  -s LEGACY_VM_SUPPORT=1
#  -s SINGLE_FILE=1
#  -s WASM=0
#)

set(EMCC_WASM_ARGS ${EMCC_ARGS}
  -s BINARYEN_IGNORE_IMPLICIT_TRAPS=1
  -s WASM=1
)

set(EMCC_GLUE_ARGS
  -c
  -I${GDX_SRC_DIR}
  -include ../../custom_glue.cpp
  -include${GDX_HEADER_FILE}
)

#######################################
project("gdx")
add_subdirectory(gdx EXCLUDE_FROM_ALL)

#######################################
add_custom_command(
  OUTPUT glue.cpp glue.js
  BYPRODUCTS parser.out WebIDLGrammar.pkl
  COMMAND ${PYTHON} ${WEBIDL_BINDER_SCRIPT} ${GDX_IDL_FILE} glue
  DEPENDS ${GDX_IDL_FILE}
  COMMENT "Generating gdx bindings"
  VERBATIM
)
add_custom_command(
  OUTPUT glue.o
  COMMAND em++ glue.cpp ${EMCC_GLUE_ARGS} -o glue.o
  DEPENDS glue.cpp ${GDX_HEADER_FILE}
  COMMENT "Building gdx bindings"
  VERBATIM
)
add_custom_target(gdx-bindings ALL DEPENDS glue.js glue.o)

#######################################
#add_custom_command(
#  OUTPUT gdx.js
#  COMMAND em++ glue.o ${GDX_LIBS} ${EMCC_JS_ARGS} -o gdx.js
#  DEPENDS gdx-bindings ${GDX_TARGETS}
#  COMMENT "Building gdx javascript"
#  VERBATIM
#)
#add_custom_target(gdx-javascript ALL DEPENDS gdx.js)

#######################################
add_custom_command(
  OUTPUT gdx.wasm.js gdx.wasm.wasm
  COMMAND emcc glue.o ${GDX_LIBS} ${EMCC_WASM_ARGS} -o gdx.wasm.js
  DEPENDS gdx-bindings ${GDX_TARGETS}
  COMMENT "Building gdx webassembly"
  VERBATIM
)
add_custom_target(gdx-wasm ALL DEPENDS gdx.wasm.js gdx.wasm.wasm)