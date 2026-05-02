#include "PSPInclude.h"
#include "PSPDebugApi.h"
#include "PSPGraphicsApi.h"
#include "PSPCoreApi.h"

#define STB_IMAGE_IMPLEMENTATION
#include "stb_image.h"

PSP_MODULE_INFO("${PROJECT_NAME}", 0, 1, 1);
PSP_MAIN_THREAD_ATTR(THREAD_ATTR_USER);

#include "all.c"