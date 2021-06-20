package com.github.xpenatan.gdx.html5.generator.utils.server;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;

public class JettyServer {

    Server server;
    WebAppContext context;

    private boolean serverStarted = false;

    private String webAppDirectory;

    public void runServer() {
        int port = 8080;
        server = new Server(port);
        context = new WebAppContext();
        context.setResourceBase(webAppDirectory);

        context.setCopyWebDir(false);
        context.setContextPath("/");
        context.setParentLoaderPriority(false);

        // Prevent windows file locking
        context.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer", "false");

        context.setPersistTempDirectory(false);
        server.setHandler(context);
        try {
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
            serverStarted = false;
        }
    }

    public void startServer() {
        if(!serverStarted && webAppDirectory != null) {
            File file = new File(webAppDirectory);
            if(!file.exists()) {
                //webapp don't exist
                return;
            }
            serverStarted = true;
            new Thread() {
                @Override
                public void run() {
                    runServer();
                }
            }.start();
        }
    }

    public void stopServer() {
        if(this.server != null) {
            Server server = this.server;
            this.server = null;

            try {
                context.stop();
                context.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                server.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }
            server.setHandler(null);
            server.destroy();
            serverStarted = false;
        }
    }

    public void setWebAppDirectory(String path) {
        this.webAppDirectory = path + "\\webapp";
    }

    public boolean isServerRunning() {
        return serverStarted;
    }
}
