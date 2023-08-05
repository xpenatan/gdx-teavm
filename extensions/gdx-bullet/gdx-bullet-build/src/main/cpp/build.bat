cmake -G "MinGW Makefiles" -B ./build/emscripten/
cmake --build ./build/emscripten/ -- VERBOSE=1
XCOPY build\emscripten\bullet.js ..\..\gdx-bullet-teavm\src\main\resources\ /y
XCOPY build\emscripten\bullet.wasm.js ..\..\gdx-bullet-teavm\src\main\resources\ /y