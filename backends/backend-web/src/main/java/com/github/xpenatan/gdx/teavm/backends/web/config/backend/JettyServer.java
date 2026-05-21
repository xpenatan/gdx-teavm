package com.github.xpenatan.gdx.teavm.backends.web.config.backend;

import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import java.io.File;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    private volatile Server server;
    private volatile WebAppContext context;

    private volatile boolean serverStarted = false;

    private String webAppDirectory;

    private int port;

    private void runServer() {
        server = new Server(port);
        context = new WebAppContext();
        context.setResourceBase(webAppDirectory);

        context.setCopyWebDir(false);
        context.setContextPath("/");
        context.setWelcomeFiles(new String[] { "index.html" });
        context.setParentLoaderPriority(false);
        context.addServlet(DefaultServlet.class, "/");

        // Prevent windows file locking
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        context.setPersistTempDirectory(false);
        server.setHandler(context);
        try {
            server.start();
            serverStarted = true;
            TeaLogHelper.logHeader("Jetty Dev Server started at http://localhost:" + port + "/");
        }
        catch(Exception e) {
            e.printStackTrace();
            stopServer();
        }
    }

    public synchronized void startServer(int port, String path, boolean runInSeparateThread) {
        if(!serverStarted && path != null && !path.isEmpty()) {
            stopServer();
            this.port = port;
            this.webAppDirectory = path;
            File file = new File(webAppDirectory);
            if(!file.exists()) {
                //webapp don't exist
                throw new RuntimeException("Webapp directory doesn't exist: " + webAppDirectory);
            }
            if(runInSeparateThread) {
                new Thread() {
                    @Override
                    public void run() {
                        runServer();
                    }
                }.start();
            }
            else {
                runServer();
            }
        }
    }

    public synchronized void stopServer() {
        Server server = this.server;
        WebAppContext context = this.context;
        this.server = null;
        this.context = null;
        serverStarted = false;

        if(context != null) {
            try {
                context.stop();
                context.destroy();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
        }

        if(server != null) {
            try {
                server.stop();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            server.setHandler(null);
            server.destroy();
        }
    }

    public boolean isServerRunning() {
        return serverStarted;
    }

    public String getWebAppDirectory() {
        return webAppDirectory;
    }
}
