#include "ios_bridge.h"

#include <stddef.h>

#if !defined(_WIN32)
#include <unistd.h>
#endif

int gdx_teavm_ios_teavm_main(int argc, char** argv);

int32_t gdx_teavm_ios_start(const char* workingDirectory) {
#if !defined(_WIN32)
    if(workingDirectory != NULL && workingDirectory[0] != '\0') {
        chdir(workingDirectory);
    }
#else
    (void) workingDirectory;
#endif
    char* argv[] = { "gdx-teavm-ios" };
    return (int32_t)gdx_teavm_ios_teavm_main(1, argv);
}
