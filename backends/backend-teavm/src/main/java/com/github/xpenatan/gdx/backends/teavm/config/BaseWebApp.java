package com.github.xpenatan.gdx.backends.teavm.config;

import com.github.xpenatan.gdx.backends.teavm.TeaClassLoader;
import java.util.ArrayList;

public class BaseWebApp {
    public String webXML;
    public String indexHtml;
    public final ArrayList<String> rootAssets = new ArrayList<>();

    public void setup(TeaClassLoader classLoader, TeaBuildConfiguration config) {
    }
}