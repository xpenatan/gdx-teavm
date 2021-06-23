package com.github.xpenatan.gdx.html5.generator.core.viewmodel;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.html5.generator.core.utils.server.JettyServer;
import com.github.xpenatan.imgui.ImGuiBoolean;
import com.github.xpenatan.imgui.ImGuiString;

import java.io.File;
import java.net.URL;

public class GeneratorViewModel {
    private JettyServer server;

    public final ImGuiString gameJarPath;
    public final ImGuiString appClassName;
    public final ImGuiString assetsDirectory;
    public final ImGuiString webappDirectory;
    public final ImGuiBoolean obfuscateFlag;

    private boolean isCompiling;

    public GeneratorViewModel() {
        server = new JettyServer();

        gameJarPath = new ImGuiString("");
        appClassName = new ImGuiString("");
        assetsDirectory = new ImGuiString("");
        webappDirectory = new ImGuiString("");
        obfuscateFlag = new ImGuiBoolean();
    }

    public boolean isCompiling() {
        return isCompiling;
    }

    private boolean validateInputs() {
        boolean flag;
        // TODO Improve
        flag = !gameJarPath.getValue().isEmpty();
        flag = flag && !appClassName.getValue().isEmpty() && validateWebappDirectory();
        return flag;
    }

    private boolean validateWebappDirectory() {
        return !webappDirectory.getValue().isEmpty();
    }

    public void compile() {
        if(validateInputs()) {
            isCompiling = true;
            try {
                String appClassName = this.appClassName.getValue();
                String jarPath = gameJarPath.getValue();
                String assetPath = assetsDirectory.getValue();
                String webappDestination = webappDirectory.getValue();
                boolean obfuscate = obfuscateFlag.getValue();

                URL appJarAppUrl = new File(jarPath).toURI().toURL();
                TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
                if(!assetPath.isEmpty())
                    teaBuildConfiguration.assetsPath.add(new File(assetPath));
                teaBuildConfiguration.webappPath = webappDestination;
                teaBuildConfiguration.obfuscate = obfuscate;
                teaBuildConfiguration.additionalClasspath.add(appJarAppUrl);
                teaBuildConfiguration.mainApplicationClass = appClassName;

                new Thread() {
                    @Override
                    public void run() {
                        TeaBuilder.build(teaBuildConfiguration);
                        isCompiling = false;
                    }
                }.start();
            } catch (Exception e) {
                e.printStackTrace();
                isCompiling = false;
            }
        }
    }

    public boolean isServerRunning() {
        return server.isServerRunning();
    }

    public boolean startLocalServer() {
        if(validateWebappDirectory()) {
            String webappDirectory = this.webappDirectory.getValue();
            server.startServer(webappDirectory);
            return true;
        }
        return false;
    }

    public void stopLocalServer() {
        server.stopServer();
    }

    public void dispose() {
        server.stopServer();
    }

//    private ArrayList<String> getClassNamesFromJar(File file) {
//        ArrayList<String> classNames = new ArrayList<>();
//        try {
//            JarInputStream jarFile = new JarInputStream(new FileInputStream(file));
//            //Iterate through the contents of the jar file
//            while (true) {
//                JarEntry jar = jarFile.getNextJarEntry();
//                if (jar == null) {
//                    break;
//                }
//                //Pick file that has the extension of .class
//                if ((jar.getName().endsWith(".class"))) {
//                    String className = jar.getName().replaceAll("/", "\\.");
//                    String myClass = className.substring(0, className.lastIndexOf('.'));
//                    classNames.add(myClass);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return classNames;
//    }

}
