package com.github.xpenatan.gdx.teavm.backends.web.config.backend;

import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import java.io.File;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;

public class JettyServer {

    private Server server;
    private WebAppContext context;

    private boolean serverStarted = false;

    private String webAppDirectory;

    private int port;

    private void runServer() {
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
            serverStarted = true;
            TeaLogHelper.logHeader("Jetty Dev Server started at http://localhost:" + port + "/");
        }
        catch(Exception e) {
            e.printStackTrace();
            serverStarted = false;
        }
    }

    public void startServer(int port, String path, boolean runInSeparateThread) {
        if(!serverStarted && path != null && !path.isEmpty()) {
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

    public void stopServer() {
        if(this.server != null) {
            Server server = this.server;
            this.server = null;

            try {
                context.stop();
                context.destroy();
            }
            catch(Exception e) {
                e.printStackTrace();
            }

            try {
                server.stop();
            }
            catch(Exception e) {
                e.printStackTrace();
            }
            server.setHandler(null);
            server.destroy();
            serverStarted = false;
        }
    }

    public boolean isServerRunning() {
        return serverStarted;
    }

    public String getWebAppDirectory() {
        return webAppDirectory;
    }
}
