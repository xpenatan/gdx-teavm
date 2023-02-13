package com.github.xpenatan.gdx.backends.teavm.util;

import com.github.xpenatan.gdx.backends.teavm.TeaApplication;

@Deprecated
public interface WebJSApplication {
    void initBulletPhysics(TeaApplication application);

    void initBox2dPhysics(TeaApplication application);

    void initImGui(TeaApplication application);
}