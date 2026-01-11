cmake -G "MinGW Makefiles" -B ./build/emscripten/
cmake --build ./build/emscripten/ -- VERBOSE=1
XCOPY build\emscripten\gdx.js ..\src\main\resources\ /y
XCOPY build\emscripten\gdx.wasm.js ..\src\main\resources\ /y
XCOPY build\emscripten\gdx.wasm.wasm ..\src\main\resources\ /y