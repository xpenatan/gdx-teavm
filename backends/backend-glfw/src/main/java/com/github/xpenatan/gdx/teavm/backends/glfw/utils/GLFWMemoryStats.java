package com.github.xpenatan.gdx.teavm.backends.glfw.utils;

import org.teavm.interop.Import;

public final class GLFWMemoryStats {

    private GLFWMemoryStats() {
    }

    @Import(name = "teavm_memory_heap_used_bytes")
    public static native long getHeapUsedBytes();

    @Import(name = "teavm_memory_heap_free_bytes")
    public static native long getHeapFreeBytes();

    @Import(name = "teavm_memory_heap_committed_bytes")
    public static native long getHeapCommittedBytes();

    @Import(name = "teavm_memory_heap_max_bytes")
    public static native long getHeapMaxBytes();

    @Import(name = "teavm_memory_direct_buffer_live_bytes")
    public static native long getDirectBufferLiveBytes();

    @Import(name = "teavm_memory_direct_buffer_count")
    public static native int getDirectBufferCount();

    public static String getSummary() {
        return "heap used=" + formatBytes(getHeapUsedBytes())
                + " free=" + formatBytes(getHeapFreeBytes())
                + " committed=" + formatBytes(getHeapCommittedBytes())
                + " max=" + formatBytes(getHeapMaxBytes())
                + " directBuffers=" + formatBytes(getDirectBufferLiveBytes())
                + " directBufferCount=" + getDirectBufferCount();
    }

    public static String formatBytes(long bytes) {
        if(bytes < 0) {
            bytes = 0;
        }
        long kib = bytes / 1024;
        long whole = kib / 1024;
        long fraction = ((kib % 1024) * 100) / 1024;
        return whole + "." + (fraction < 10 ? "0" : "") + fraction + " MiB";
    }
}
