#include "PSPInclude.h"
#include "PSPDebugApi.h"
#include "PSPGraphicsApi.h"
#include "PSPCoreApi.h"

PSP_MODULE_INFO("${PROJECT_NAME}", 0, 1, 1);
PSP_MAIN_THREAD_ATTR(THREAD_ATTR_USER);

#include "all.c"


//#include <pspuser.h>
//#include <pspdisplay.h>
//#include <pspdebug.h>
//#include <stdlib.h>
//#include <stdio.h>
//#include <math.h>
//#include <string.h>
//
//#include <pspgu.h>
//#include <pspgum.h>
//
//PSP_MODULE_INFO("Cube Sample", 0, 1, 1);
//PSP_MAIN_THREAD_ATTR(THREAD_ATTR_USER);
//
//unsigned char logo_start[];
//
//#define BUF_WIDTH (512)
//#define SCR_WIDTH (480)
//#define SCR_HEIGHT (272)
//
//int main(int argc, char* argv[])
//{
//	setupCallbacks();
//
//	// setup GU
//	initGraphics();
//
//	// run sample
//
//	int val = 0;
//
//	while(isRunning())
//	{
//		beginFrame(0);
//
//		// clear screen
//
//		sceGuClearColor(0xff554433);
//		sceGuClearDepth(0);
//		sceGuClear(GU_COLOR_BUFFER_BIT|GU_DEPTH_BUFFER_BIT);
//
//		// setup matrices for cube
//
//		sceGumMatrixMode(GU_PROJECTION);
//		sceGumLoadIdentity();
//		sceGumPerspective(75.0f,16.0f/9.0f,0.5f,1000.0f);
//
//		sceGumMatrixMode(GU_VIEW);
//		sceGumLoadIdentity();
//
//		sceGumMatrixMode(GU_MODEL);
//		sceGumLoadIdentity();
//		{
//			ScePspFVector3 pos = { 0, 0, -2.5f };
//			ScePspFVector3 rot = { val * 0.79f * (GU_PI/180.0f), val * 0.98f * (GU_PI/180.0f), val * 1.32f * (GU_PI/180.0f) };
//			sceGumTranslate(&pos);
//			sceGumRotateXYZ(&rot);
//		}
//
//		// setup texture
//
//		sceGuTexMode(GU_PSM_4444,0,0,0);
//		sceGuTexImage(0,64,64,64,logo_start);
//		sceGuTexFunc(GU_TFX_ADD,GU_TCC_RGB);
//		sceGuTexEnvColor(0xffff00);
//		sceGuTexFilter(GU_LINEAR,GU_LINEAR);
//		sceGuTexScale(1.0f,1.0f);
//		sceGuTexOffset(0.0f,0.0f);
//		sceGuAmbientColor(0xffffffff);
//
//		// draw cube
//
//		sceGumDrawArray(GU_TRIANGLES,GU_TEXTURE_32BITF|GU_COLOR_8888|GU_VERTEX_32BITF|GU_TRANSFORM_3D,12*3,0,vertices);
//
//
//        endFrame(true, 0);
//
//		val++;
//	}
//
//	sceGuTerm();
//
//	sceKernelExitGame();
//	return 0;
//}