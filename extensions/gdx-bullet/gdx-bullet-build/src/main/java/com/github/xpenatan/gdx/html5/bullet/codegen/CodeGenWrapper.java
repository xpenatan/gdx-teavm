package com.github.xpenatan.gdx.html5.bullet.codegen;

/** @author xpenatan */
public interface CodeGenWrapper {
    String wrap(boolean isStatic, String className, String methodName, String returnType, String params, String content, String indentLevel);
}