package com.github.xpenatan.examples.dragome.launcher;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.tests.html.examples.GearsDemo;
import com.dragome.web.annotations.PageAlias;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplication;
import com.github.xpenatan.gdx.backends.dragome.DragomeApplicationConfiguration;
import com.github.xpenatan.gdx.backends.dragome.DragomeWindow;

@PageAlias(alias = "Gears")
public class GearsLauncher extends DragomeApplication {
    @Override
    public ApplicationListener createApplicationListener() {
        return new GearsDemo();
    }

    @Override
    public DragomeApplicationConfiguration getConfig() {
        return null;
    }

    @Override
    protected void onResize() {
        int clientWidth = DragomeWindow.getInnerWidth();
        int clientHeight = DragomeWindow.getInnerHeight();
        getCanvas().setWidth(clientWidth);
        getCanvas().setHeight(clientHeight);
        getCanvas().setCoordinateSpaceWidth(clientWidth);
        getCanvas().setCoordinateSpaceHeight(clientHeight);
        super.onResize();
    }
}
