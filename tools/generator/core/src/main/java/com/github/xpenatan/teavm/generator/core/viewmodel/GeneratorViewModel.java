package com.github.xpenatan.teavm.generator.core.viewmodel;

import com.github.xpenatan.gdx.backends.teavm.TeaBuildConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaBuilder;
import com.github.xpenatan.teavm.generator.core.utils.server.JettyServer;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.teavm.tooling.TeaVMTool;

public class GeneratorViewModel {
    private JettyServer server;

    private boolean isCompiling;
    private boolean isError;

    private float progress;

    public GeneratorViewModel() {
        server = new JettyServer();
    }

    public boolean isCompiling() {
        return isCompiling;
    }

    private boolean validateInputs(String gameJarPathStr, String appClassNameStr, String webappDirectoryStr) {
        boolean flag;
        // TODO Improve
        flag = !gameJarPathStr.isEmpty();
        flag = flag && !appClassNameStr.isEmpty() && validateWebappDirectory(webappDirectoryStr);
        return flag;
    }

    private boolean validateWebappDirectory(String webappDirectory) {
        return !webappDirectory.isEmpty();
    }

    public void compile(String gameJarPathStr, String appClassNameStr, String assetsDirectoryStr, String webappDirectoryStr, Boolean obfuscateFlagStr) {
        if(!isCompiling && validateInputs(gameJarPathStr, appClassNameStr, webappDirectoryStr)) {
            String appClassName = appClassNameStr;
            String jarPath = gameJarPathStr;
            String assetPath = assetsDirectoryStr;
            String webappDestination = webappDirectoryStr;
            boolean obfuscate = obfuscateFlagStr;

            try {
                URL appJarAppUrl = new File(jarPath).toURI().toURL();
                TeaBuildConfiguration teaBuildConfiguration = new TeaBuildConfiguration();
                if(!assetPath.isEmpty())
                    teaBuildConfiguration.assetsPath.add(new File(assetPath));
                teaBuildConfiguration.webappPath = webappDestination;
                teaBuildConfiguration.obfuscate = obfuscate;
                teaBuildConfiguration.additionalClasspath.add(appJarAppUrl);
                teaBuildConfiguration.setApplicationListener(appClassName);
                TeaVMTool tool = config(teaBuildConfiguration);
                compile(tool, teaBuildConfiguration);
            }
            catch(MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    public TeaVMTool config(TeaBuildConfiguration teaBuildConfiguration) {
        TeaVMTool tool = TeaBuilder.config(teaBuildConfiguration, new TeaBuilder.TeaProgressListener() {
            @Override
            public void onProgress(float progress) {
                GeneratorViewModel.this.progress = progress;
            }
        });
        return tool;
    }

    public void compile(TeaVMTool tool, TeaBuildConfiguration teaBuildConfiguration) {
        if(!isCompiling) {
            isCompiling = true;
            isError = false;
            progress = 0;
            try {
                new Thread() {
                    @Override
                    public void run() {
                        boolean serverRunning = server.isServerRunning();
                        stopLocalServer();

                        boolean isSuccess = TeaBuilder.build(tool);
                        isError = !isSuccess;

                        isCompiling = false;
                        if(serverRunning)
                            startLocalServer(teaBuildConfiguration.webappPath);
                    }
                }.start();
            }
            catch(Exception e) {
                e.printStackTrace();
                isCompiling = false;
                isError = true;
            }
        }
    }

    public boolean isServerRunning() {
        return server.isServerRunning();
    }

    public boolean startLocalServer(String webappDirectory) {
        if(validateWebappDirectory(webappDirectory)) {
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

    public float getProgress() {
        return progress;
    }

    public boolean getError() {
        return isError;
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
