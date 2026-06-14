package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import java.util.function.Predicate;
import org.teavm.extension.spi.substitution.SimpleSubstitutionPolicy;
import org.teavm.extension.spi.substitution.SubstitutionSink;

public class GdxTeaVMWebSubstitutionPolicy extends SimpleSubstitutionPolicy {
    @Override
    public void contribute(SubstitutionSink sink) {
        Predicate<String> libGdxClasses = inPackage("com.badlogic.gdx", true);
        Predicate<String> gltfClasses = inPackage("net.mgsx", true);
        Predicate<String> jBox2dClasses = inPackage("org.jbox2d", true);
        Predicate<String> webFileChannelClasses = inPackage("java.nio.channels", true);

        sink.selectClasses(libGdxClasses).packagePrefix("emu");
        sink.selectClasses(gltfClasses).packagePrefix("emu");
        sink.selectClasses(jBox2dClasses).packagePrefix("emu");
        sink.selectClasses(webFileChannelClasses).packagePrefix("emu");
    }
}
