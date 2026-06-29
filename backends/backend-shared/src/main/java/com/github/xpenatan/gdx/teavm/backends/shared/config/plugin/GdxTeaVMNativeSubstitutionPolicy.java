package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.util.function.Predicate;
import org.teavm.extension.spi.substitution.SimpleSubstitutionPolicy;
import org.teavm.extension.spi.substitution.SubstitutionSink;

public class GdxTeaVMNativeSubstitutionPolicy extends SimpleSubstitutionPolicy {
    @Override
    public void contribute(SubstitutionSink sink) {
        Predicate<String> glfwControllerClasses = inPackage("com.badlogic.gdx.controllers", true);
        Predicate<String> cFreeTypeClasses = inPackage("com.badlogic.gdx.graphics.g2d.freetype", true);
        Predicate<String> nativeExtensionClasses = glfwControllerClasses.or(cFreeTypeClasses);
        Predicate<String> libGdxClasses = inPackage("com.badlogic.gdx", true).and(nativeExtensionClasses.negate());
        Predicate<String> gltfClasses = inPackage("net.mgsx", true);

        sink.selectClasses(glfwControllerClasses).packagePrefix("emu.glfw");
        sink.selectClasses(cFreeTypeClasses).packagePrefix("emu.c");
        sink.selectClasses(libGdxClasses).packagePrefix("emu").simpleNamePrefix("T");
        sink.selectClasses(gltfClasses).packagePrefix("emu").simpleNamePrefix("T");
    }
}
