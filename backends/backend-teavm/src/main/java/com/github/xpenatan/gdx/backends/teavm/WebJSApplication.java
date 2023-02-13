package com.github.xpenatan.gdx.backends.teavm;

public interface WebJSApplication {
    void initBulletPhysics(WebApplication application);

    void initBox2dPhysics(WebApplication application);

    void initImGui(WebApplication application);
}