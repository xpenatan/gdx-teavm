#pragma once

#include <vramalloc.h>

static unsigned int __attribute__((aligned(16))) list[262144];

#define PSP_BUF_WIDTH (512)
#define PSP_SCR_WIDTH (480)
#define PSP_SCR_HEIGHT (272)

void grInitGraphics() {
    void* fbp0 = guGetStaticVramBuffer(PSP_BUF_WIDTH, PSP_SCR_HEIGHT, GU_PSM_8888);
    void* fbp1 = guGetStaticVramBuffer(PSP_BUF_WIDTH, PSP_SCR_HEIGHT, GU_PSM_8888);
    void* zbp = guGetStaticVramBuffer(PSP_BUF_WIDTH, PSP_SCR_HEIGHT, GU_PSM_4444);

    sceGuInit();

    sceGuStart(GU_DIRECT, list);
    sceGuDrawBuffer(GU_PSM_8888, fbp0, PSP_BUF_WIDTH);
    sceGuDispBuffer(PSP_SCR_WIDTH, PSP_SCR_HEIGHT, fbp1, PSP_BUF_WIDTH);
    sceGuDepthBuffer(zbp, PSP_BUF_WIDTH);
    sceGuOffset(2048 - (PSP_SCR_WIDTH / 2), 2048 - (PSP_SCR_HEIGHT / 2));
    sceGuViewport(2048, 2048, PSP_SCR_WIDTH, PSP_SCR_HEIGHT);
    sceGuDepthRange(0, 65535);
    sceGuScissor(0, 0, PSP_SCR_WIDTH, PSP_SCR_HEIGHT);
    sceGuEnable(GU_SCISSOR_TEST);
    sceGuDepthFunc(GU_GEQUAL);
    sceGuEnable(GU_DEPTH_TEST);
    sceGuFrontFace(GU_CW);
    sceGuShadeModel(GU_SMOOTH);
    sceGuEnable(GU_CULL_FACE);
    sceGuEnable(GU_TEXTURE_2D);
    sceGuEnable(GU_CLIP_PLANES);
    sceGuFinish();
    sceGuSync(0, 0);

    sceDisplayWaitVblankStart();
    sceGuDisplay(GU_TRUE);
}

void grStartFrame(int dialog) {
    sceGuStart(GU_DIRECT, list);

    if (dialog) {
        sceGuFinish();
        sceGuSync(0, 0);
    }
}

void grSwapBuffers(int vsync, int dialog) {
    if (!dialog) {
        sceGuFinish();
        sceGuSync(0, 0);
    }

    if (vsync) {
        sceDisplayWaitVblankStart();
    }

    sceGuSwapBuffers();
}