package com.github.xpenatan.imgui.example.tests.frame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.github.xpenatan.gdx.multiview.EmuApplicationWindow;

public class GameFrame {
    public int windowX;
    public int windowY;
    public int windowWidth;
    public int windowHeight;
    public EmuApplicationWindow emuWindow;
    public boolean isWindowFocused = false;
    public boolean isWindowHovered = false;

    public GameFrame(int windowX, int windowY, int windowWidth, int windowHeight) {
        this.windowX = windowX;
        this.windowY = windowY;
        this.windowWidth = windowWidth;
        this.windowHeight = windowHeight;
        emuWindow = new EmuApplicationWindow();
    }

    public void update() {
        Rectangle.tmp.set(windowX, windowY, windowWidth, windowHeight);
        isWindowHovered = Rectangle.tmp.contains(Gdx.input.getX(), Gdx.input.getY());
        if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT))
            isWindowFocused = Rectangle.tmp.contains(Gdx.input.getX(), Gdx.input.getY());
        emuWindow.begin(isWindowFocused, isWindowHovered, windowX, windowY, windowWidth, windowHeight);
        emuWindow.loop();
        emuWindow.end();
    }

    public void draw(SpriteBatch batch) {
        Texture texture = emuWindow.getTexture();
        batch.draw(texture, windowX, windowY, windowWidth, windowHeight, emuWindow.u, emuWindow.v, emuWindow.u2, emuWindow.v2);
    }
}