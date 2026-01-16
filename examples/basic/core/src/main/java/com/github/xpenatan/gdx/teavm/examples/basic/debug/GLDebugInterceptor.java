package com.github.xpenatan.gdx.teavm.examples.basic.debug;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.FloatCounter;
import java.util.HashMap;
import java.util.Map;

public abstract class GLDebugInterceptor implements GL20 {

    protected int calls;
    protected int textureBindings;
    protected int drawCalls;
    protected int shaderSwitches;
    protected final FloatCounter vertexCount = new FloatCounter(0);

    protected Map<String, Integer> methodCounts = new HashMap<>();

    protected GLDebugProfiler glProfiler;

    protected GLDebugInterceptor(GLDebugProfiler profiler) {
        this.glProfiler = profiler;
    }

    protected void incrementMethod(String methodName) {
        methodCounts.put(methodName, methodCounts.getOrDefault(methodName, 0) + 1);
    }

    public void printMethodCounts() {
        methodCounts.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> System.out.println(entry.getKey() + ": " + entry.getValue()));
    }

    public static String resolveErrorNumber(int error) {
        switch(error) {
            case GL_INVALID_VALUE:
                return "GL_INVALID_VALUE";
            case GL_INVALID_OPERATION:
                return "GL_INVALID_OPERATION";
            case GL_INVALID_FRAMEBUFFER_OPERATION:
                return "GL_INVALID_FRAMEBUFFER_OPERATION";
            case GL_INVALID_ENUM:
                return "GL_INVALID_ENUM";
            case GL_OUT_OF_MEMORY:
                return "GL_OUT_OF_MEMORY";
            default:
                return "number " + error;
        }
    }

    public int getCalls() {
        return calls;
    }

    public int getTextureBindings() {
        return textureBindings;
    }

    public int getDrawCalls() {
        return drawCalls;
    }

    public int getShaderSwitches() {
        return shaderSwitches;
    }

    public FloatCounter getVertexCount() {
        return vertexCount;
    }

    public void reset() {
        calls = 0;
        textureBindings = 0;
        drawCalls = 0;
        shaderSwitches = 0;
        vertexCount.reset();
        methodCounts.clear();
    }
}
