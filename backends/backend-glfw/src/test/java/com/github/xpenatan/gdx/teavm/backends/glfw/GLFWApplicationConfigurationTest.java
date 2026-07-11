package com.github.xpenatan.gdx.teavm.backends.glfw;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;

import com.github.xpenatan.gdx.teavm.backends.glfw.graphics.GLFWGraphicsFactory;
import org.junit.Test;

public class GLFWApplicationConfigurationTest {
    @Test
    public void customFactorySelectsNoApiAndOpenGLEmulationRestoresDefaultFactory() {
        GLFWApplicationConfiguration config = new GLFWApplicationConfiguration();
        GLFWGraphicsFactory customFactory = window -> null;

        config.setGraphicsFactory(customFactory);
        assertEquals(GLFWApplicationConfiguration.ClientApi.NO_API, config.getClientApi());
        assertSame(customFactory, config.getGraphicsFactory());

        config.setOpenGLEmulation(GLFWApplicationConfiguration.GLEmulation.GL30, 3, 2);
        assertEquals(GLFWApplicationConfiguration.ClientApi.OPENGL, config.getClientApi());
        assertNotSame(customFactory, config.getGraphicsFactory());
    }
}
