package com.github.xpenatan.gdx.teavm.backends.web.config;

/**
 * Compile-time defaults consumed by the web runtime. TeaVM replaces these accessor bodies with values supplied by
 * the selected web backend while leaving useful JVM defaults for ordinary tests and tooling.
 */
public final class TeaWebRuntimeConfig {

    private TeaWebRuntimeConfig() {
    }

    public static String getStartupLogo() {
        return "startup-logo.png";
    }
}
