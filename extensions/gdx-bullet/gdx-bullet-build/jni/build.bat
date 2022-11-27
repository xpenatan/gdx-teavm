cmake -G "MinGW Makefiles" -B ./build/emscripten/
cmake --build ./build/emscripten/ -- VERBOSE=1
XCOPY build\emscripten\bullet.js ..\..\gdx-bullet-teavm\resources\ /y
XCOPY build\emscripten\bullet.wasm.js ..\..\gdx-bullet-teavm\resources\ /y
XCOPY build\emscripten\bullet.wasm.wasm ..\..\gdx-bullet-teavm\resources\ /y