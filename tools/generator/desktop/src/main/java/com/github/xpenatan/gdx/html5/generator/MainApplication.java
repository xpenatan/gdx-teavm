package com.github.xpenatan.gdx.html5.generator;

import com.github.xpenatan.teavm.generator.ui.view.GeneratorView;

public class MainApplication extends ImGuiRenderer {

    private GeneratorView window;

    @Override
    public void show() {
        super.show();
        window = new GeneratorView();
    }

    @Override
    public void renderImGui() {
        window.render();
    }

    @Override
    public void dispose() {
        window.dispose();
        super.dispose();
    }
}
