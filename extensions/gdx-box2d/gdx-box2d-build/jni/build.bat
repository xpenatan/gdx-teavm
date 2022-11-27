cmake -G "MinGW Makefiles" -B ./build/emscripten/
cmake --build ./build/emscripten/ -- VERBOSE=1
XCOPY build\emscripten\box2d.js ..\..\gdx-box2d-teavm\resources\ /y
XCOPY build\emscripten\box2d.wasm.js ..\..\gdx-box2d-teavm\resources\ /y
XCOPY build\emscripten\box2d.wasm.wasm ..\..\gdx-box2d-teavm\resources\ /y