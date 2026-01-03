package com.github.xpenatan.gdx.backends.teavm.config;

public class TeaBuilderLog {
    public static void log(String msg) {
        String text = "| " + msg;
        logInternalNewLine(text);
    }

    public static void logInternal(String msg) {
        System.err.print(msg);
    }

    public static void logInternalNewLine(String msg) {
        logInternal(msg + "\n");
    }

    public static void logHeader(String text) {
        String msg = "";
        msg += "#################################################################\n";
        msg += "|\n| " + text + "\n|";
        msg += "\n" + "#################################################################";

        logInternalNewLine(msg);
    }

    public static void logEnd() {
        String msg = "\n#################################################################";
        logInternalNewLine(msg);
    }
}