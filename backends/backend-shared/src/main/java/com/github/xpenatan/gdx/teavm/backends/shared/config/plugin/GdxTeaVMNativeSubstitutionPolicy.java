package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.util.function.Predicate;
import org.teavm.extension.spi.substitution.SimpleSubstitutionPolicy;
import org.teavm.extension.spi.substitution.SubstitutionSink;

public class GdxTeaVMNativeSubstitutionPolicy extends SimpleSubstitutionPolicy {
    @Override
    public void contribute(SubstitutionSink sink) {
        Predicate<String> libGdxClasses = inPackage("com.badlogic.gdx", true);
        Predicate<String> gltfClasses = inPackage("net.mgsx", true);

        sink.selectClasses(libGdxClasses).packagePrefix("emu").simpleNamePrefix("T");
        sink.selectClasses(gltfClasses).packagePrefix("emu").simpleNamePrefix("T");
    }
}
