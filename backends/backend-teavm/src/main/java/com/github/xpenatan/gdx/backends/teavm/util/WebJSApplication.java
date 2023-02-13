package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.WebApplication;

@Deprecated
public interface WebJSApplication {
    void initBulletPhysics(WebApplication application);

    void initBox2dPhysics(WebApplication application);

    void initImGui(WebApplication application);
}