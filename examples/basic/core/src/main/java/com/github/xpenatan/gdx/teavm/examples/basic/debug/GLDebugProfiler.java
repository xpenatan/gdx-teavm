package com.github.xpenatan.gdx.teavm.examples.basic.debug;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL31;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.badlogic.gdx.math.FloatCounter;

public class GLDebugProfiler {

    private Graphics graphics;
    private GLDebugInterceptor glInterceptor;
    private GLErrorListener listener;
    private boolean enabled = false;

    public GLDebugProfiler(Graphics graphics) {
        this.graphics = graphics;
        GL32 gl32 = graphics.getGL32();
        GL31 gl31 = graphics.getGL31();
        GL30 gl30 = graphics.getGL30();
        if(gl32 != null) {
            glInterceptor = new GL32DebugInterceptor(this, gl32);
        }
        else if(gl31 != null) {
            glInterceptor = new GL31DebugInterceptor(this, gl31);
        }
        else if(gl30 != null) {
            glInterceptor = new GL30DebugInterceptor(this, gl30);
        }
        else {
            glInterceptor = new GL20DebugInterceptor(this, graphics.getGL20());
        }
        listener = GLErrorListener.LOGGING_LISTENER;
    }

    /**
     * Enables profiling by replacing the {@code GL20} and {@code GL30} instances with profiling ones.
     */
    public void enable() {
        if(enabled) return;

        if(glInterceptor instanceof GL32) {
            graphics.setGL32((GL32)glInterceptor);
        }
        if(glInterceptor instanceof GL31) {
            graphics.setGL31((GL31)glInterceptor);
        }
        if(glInterceptor instanceof GL30) {
            graphics.setGL30((GL30)glInterceptor);
        }
        graphics.setGL20(glInterceptor);

        Gdx.gl32 = graphics.getGL32();
        Gdx.gl31 = graphics.getGL31();
        Gdx.gl30 = graphics.getGL30();
        Gdx.gl20 = graphics.getGL20();
        Gdx.gl = graphics.getGL20();

        enabled = true;
    }

    /**
     * Disables profiling by resetting the {@code GL20} and {@code GL30} instances with the original ones.
     */
    public void disable() {
        if(!enabled) return;

        if(glInterceptor instanceof GL32DebugInterceptor) {
            graphics.setGL32(((GL32DebugInterceptor)glInterceptor).gl32);
        }
        if(glInterceptor instanceof GL31DebugInterceptor) {
            graphics.setGL31(((GL31DebugInterceptor)glInterceptor).gl31);
        }
        if(glInterceptor instanceof GL30DebugInterceptor) {
            graphics.setGL30(((GL30DebugInterceptor)glInterceptor).gl30);
        }
        if(glInterceptor instanceof GL20DebugInterceptor) {
            graphics.setGL20(((GL20DebugInterceptor)graphics.getGL20()).gl20);
        }

        Gdx.gl32 = graphics.getGL32();
        Gdx.gl31 = graphics.getGL31();
        Gdx.gl30 = graphics.getGL30();
        Gdx.gl20 = graphics.getGL20();
        Gdx.gl = graphics.getGL20();

        enabled = false;
    }

    /**
     * Set the current listener for the {@link GLDebugProfiler} to {@code errorListener}
     */
    public void setListener(GLErrorListener errorListener) {
        this.listener = errorListener;
    }

    /**
     * @return the current {@link GLErrorListener}
     */
    public GLErrorListener getListener() {
        return listener;
    }

    /**
     * @return true if the GLProfiler is currently profiling
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @return the total gl calls made since the last reset
     */
    public int getCalls() {
        return glInterceptor.getCalls();
    }

    /**
     * @return the total amount of texture bindings made since the last reset
     */
    public int getTextureBindings() {
        return glInterceptor.getTextureBindings();
    }

    /**
     * @return the total amount of draw calls made since the last reset
     */
    public int getDrawCalls() {
        return glInterceptor.getDrawCalls();
    }

    /**
     * @return the total amount of shader switches made since the last reset
     */
    public int getShaderSwitches() {
        return glInterceptor.getShaderSwitches();
    }

    /**
     * @return {@link FloatCounter} containing information about rendered vertices since the last reset
     */
    public FloatCounter getVertexCount() {
        return glInterceptor.getVertexCount();
    }

    public void printMethodCounts() {
        glInterceptor.printMethodCounts();
    }

    /**
     * Will reset the statistical information which has been collected so far. This should be called after every frame. Error
     * listener is kept as it is.
     */
    public void reset() {
        glInterceptor.reset();
    }

}
