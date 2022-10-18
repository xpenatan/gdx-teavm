package com.badlogic.gdx.physics.box2d;

public class Filter {
    public short categoryBits = 0x0001;
    public short maskBits = -1;
    public short groupIndex = 0;

    public void set(Filter filter) {
        categoryBits = filter.categoryBits;
        maskBits = filter.maskBits;
        groupIndex = filter.groupIndex;
    }
}