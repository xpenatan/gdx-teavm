package com.github.xpenatan.imgui.example.tests.wrapper;

import com.badlogic.gdx.tests.AbstractTestWrapper;
import com.badlogic.gdx.tests.TeaVMGdxTests;

public class TeaVMTestWrapper extends AbstractTestWrapper {
    @Override
    protected Instancer[] getTestList() {
        return TeaVMGdxTests.getTestList();
    }
}
