cmake -G "MinGW Makefiles" -B ./build/emscripten/
cmake --build ./build/emscripten/ -- VERBOSE=1
XCOPY build\emscripten\gdx.js ..\..\gdx-teavm\resources\ /y
XCOPY build\emscripten\gdx.wasm.js ..\..\gdx-teavm\resources\ /y
XCOPY build\emscripten\gdx.wasm.wasm ..\..\gdx-teavm\resources\ /y