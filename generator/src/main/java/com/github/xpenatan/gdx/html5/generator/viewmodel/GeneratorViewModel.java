package com.github.xpenatan.gdx.html5.generator.viewmodel;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.gdx.html5.generator.utils.server.JettyServer;

import java.io.File;
import java.net.URL;

public class GeneratorViewModel {
    private JettyServer server;
    private String jarPath;
    private String className;
    private String assetPath;
    private String webappDestination;
    private boolean obfuscate;

    private boolean isCompiling;

    public GeneratorViewModel() {
        server = new JettyServer();
    }

    public void setObfuscate(boolean flag) {
        obfuscate = flag;
    }

    public void setJarPath(String jarPath) {
        this.jarPath = jarPath;
    }

    public void setApplicationClass(String className) {
        this.className = className;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public void setWebAppDirectory(String webappDestination) {
        this.webappDestination = webappDestination;
    }

    public boolean isCompiling() {
        return isCompiling;
    }

    private boolean validateInputs() {
        boolean flag;
        // TODO Improve
        flag = jarPath != null && className != null;
        flag = flag && !jarPath.isEmpty() && !className.isEmpty() && validateWebappDirectory();
        return flag;
    }

    private boolean validateWebappDirectory() {
        return webappDestination != null && !webappDestination.isEmpty();
    }

    public void compile() {
        if(validateInputs()) {
            isCompiling = true;
            try {
                URL appJarAppUrl = new File(jarPath).toURI().toURL();
                TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
                if(assetPath != null && !assetPath.trim().isEmpty())
                    teaBuildConfiguration.assetsPath.add(new File(assetPath));
                teaBuildConfiguration.webappPath = webappDestination;
                teaBuildConfiguration.obfuscate = obfuscate;
                teaBuildConfiguration.additionalClasspath.add(appJarAppUrl);
                teaBuildConfiguration.mainApplicationClass = className;

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
            server.setWebAppDirectory(webappDestination);
            server.startServer();
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
