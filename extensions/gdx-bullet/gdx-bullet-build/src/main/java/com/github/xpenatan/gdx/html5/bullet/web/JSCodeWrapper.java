package com.github.xpenatan.gdx.html5.bullet.web;

public interface JSCodeWrapper {
    String wrap(boolean isStatic, String className, String methodName, String returnType, String params, String content, String indentLevel);
}