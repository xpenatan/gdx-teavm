package com.github.xpenatan.gdx.teavm.backends.psp.utils;

import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("PSPDebugApi.h")
public class PSPDebugApi {

    @Import(name = "pspDebugScreenInit")
    public static native void pspDebugScreenInit();

    @Import(name = "pspDebugScreenPrintf")
    public static native void pspDebugScreenPrintf(String text);

    @Import(name = "pspDebugScreenSetXY")
    public static native void pspDebugScreenSetXY(int x, int y);

    /**
     * @return The amount of user memory allocated in bytes.
     */
    @Import(name = "getAllocatedMemory")
    private static native int getAllocatedMemory();

    public static float getAllocatedMemoryMB() {
        double mb = getAllocatedMemory() / (1024.0 * 1024.0);
        return (float)(Math.round(mb * 100) / 100.0);
    }

    private static long lastMemoryLogTime;

    public static void logUsedMemory(int logMemoryDelayMilli) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastMemoryLogTime >= logMemoryDelayMilli) {
            float usedMemory= getAllocatedMemoryMB();
            System.out.println("Used memory: " + String.format("%.2f", usedMemory) + " MB");
            lastMemoryLogTime = currentTime;
        }
    }
}