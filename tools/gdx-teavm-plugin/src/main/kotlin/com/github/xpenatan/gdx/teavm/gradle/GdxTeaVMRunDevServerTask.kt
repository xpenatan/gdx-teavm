package com.github.xpenatan.gdx.teavm.gradle

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import org.gradle.deployment.internal.Deployment
import org.gradle.deployment.internal.DeploymentHandle
import org.gradle.deployment.internal.DeploymentRegistry
import org.gradle.internal.logging.slf4j.ContextAwareTaskLogger
import org.gradle.work.DisableCachingByDefault
import org.teavm.gradle.tasks.DevServerManager
import org.teavm.gradle.tasks.DevServerTask
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal const val GDX_TEAVM_DEV_SERVER_INDEX_FILE = "__gdx_teavm_index.html"
internal const val TEAVM_DEV_SERVER_STDERR_PREFIX = "server stderr:"

/**
 * TeaVM forwards every line from its child process' stderr as a Gradle WARN message. Gradle sends
 * WARN messages to stdout, which loses the red stderr rendering used by IDE consoles. Re-emit only
 * these forwarded messages at ERROR level so Gradle sends their contents back to stderr. TeaVM's
 * redundant "server stderr:" label is removed while the original message remains red.
 */
internal fun restoreTeaVMDevServerStderr(task: DevServerTask) {
    val taskLogger = task.logger
    val contextLogger = taskLogger as? ContextAwareTaskLogger
        ?: throw GradleException("The Gradle task logger cannot restore TeaVM development-server stderr output")
    contextLogger.setMessageRewriter { level, message ->
        rewriteTeaVMDevServerStderr(level, message, taskLogger::error)
    }
}

internal fun rewriteTeaVMDevServerStderr(
    level: LogLevel,
    message: String,
    reportError: (String) -> Unit
): String? {
    if(level == LogLevel.WARN && message.startsWith(TEAVM_DEV_SERVER_STDERR_PREFIX)) {
        reportError(message.removePrefix(TEAVM_DEV_SERVER_STDERR_PREFIX).trimStart())
        return null
    }
    return message
}

@DisableCachingByDefault(because = "Runs a persistent TeaVM development-server session")
abstract class GdxTeaVMRunDevServerTask : DefaultTask() {
    @get:Input
    abstract val deploymentId: Property<String>

    @get:Input
    abstract val devServerProjectPath: Property<String>

    @get:Input
    abstract val port: Property<Int>

    @get:Input
    abstract val entryServerPort: Property<Int>

    @get:Input
    abstract val autoBuild: Property<Boolean>

    @get:Input
    abstract val autoReload: Property<Boolean>

    @get:Input
    abstract val reloadEndpoint: Property<String>

    @get:Inject
    protected abstract val deploymentRegistry: DeploymentRegistry

    @TaskAction
    fun run() {
        val manager = DevServerManager.instance().getProjectManager(devServerProjectPath.get())
        val indexHtml = try {
            loadIndexHtml()
        }
        catch(t: Throwable) {
            manager.stop(logger)
            throw t
        }

        if(!autoBuild.get()) {
            runWithoutAutomaticBuild(indexHtml)
            return
        }

        val id = deploymentId.get()
        val existing = deploymentRegistry.get(id, GdxTeaVMDevServerDeployment::class.java)
        if(existing != null) {
            existing.update(
                devServerProjectPath.get(),
                entryServerPort.get(),
                indexHtml
            )
            logger.lifecycle("TeaVM development server rebuilt successfully.")
            return
        }

        deploymentRegistry.start(
            id,
            DeploymentRegistry.ChangeBehavior.NONE,
            GdxTeaVMDevServerDeployment::class.java,
            devServerProjectPath.get(),
            entryServerPort.get(),
            indexHtml
        )
    }

    private fun runWithoutAutomaticBuild(indexHtml: ByteArray) {
        val session = GdxTeaVMDevServerDeployment(
            devServerProjectPath.get(),
            entryServerPort.get(),
            indexHtml
        )
        val shutdownHook = Thread(session::stop, "gdx-teavm-dev-server-shutdown")
        Runtime.getRuntime().addShutdownHook(shutdownHook)
        try {
            session.startStandalone()
            logger.lifecycle("TeaVM development server URL: http://localhost:${port.get()}")
            CountDownLatch(1).await()
        }
        finally {
            session.stop()
            removeShutdownHook(shutdownHook)
        }
    }

    private fun removeShutdownHook(shutdownHook: Thread) {
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        }
        catch(_: IllegalStateException) {
            // JVM shutdown is already in progress.
        }
    }

    private fun loadIndexHtml(): ByteArray {
        val url = URI("http://127.0.0.1:${port.get()}/$GDX_TEAVM_DEV_SERVER_INDEX_FILE").toURL()
        val connection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5_000
        connection.readTimeout = 30_000
        connection.useCaches = false
        try {
            val status = connection.responseCode
            if(status != HttpURLConnection.HTTP_OK) {
                throw GradleException("TeaVM development server returned HTTP $status for $url")
            }
            val content = connection.inputStream.buffered().use { input -> input.readAllBytes() }
            val text = String(content, StandardCharsets.UTF_8)
            if(!text.contains("<html", ignoreCase = true)) {
                throw GradleException("TeaVM development server did not generate a valid HTML entry page at $url")
            }
            return if(autoReload.get()) {
                injectAutoReloadClient(text)
            }
            else {
                content
            }
        }
        finally {
            connection.disconnect()
        }
    }

    private fun injectAutoReloadClient(indexHtml: String): ByteArray {
        val encodedEndpoint = Base64.getEncoder().encodeToString(
            reloadEndpoint.get().toByteArray(StandardCharsets.UTF_8)
        )
        val script = """
            <script data-gdx-teavm-auto-reload>
                (() => {
                    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
                    const endpoint = atob("$encodedEndpoint");
                    const socket = new WebSocket(protocol + "//" + window.location.host + endpoint);
                    socket.addEventListener("message", event => {
                        let message;
                        try {
                            message = JSON.parse(event.data);
                        }
                        catch(ignored) {
                            return;
                        }
                        if(message.command === "complete" && message.success) {
                            window.setTimeout(() => window.location.reload(), 100);
                        }
                    });
                })();
            </script>
        """.trimIndent()
        val bodyEnd = indexHtml.lastIndexOf("</body>", ignoreCase = true)
        val updated = if(bodyEnd >= 0) {
            indexHtml.substring(0, bodyEnd) + script + "\n" + indexHtml.substring(bodyEnd)
        }
        else {
            indexHtml + "\n" + script
        }
        return updated.toByteArray(StandardCharsets.UTF_8)
    }
}

/**
 * With automatic builds enabled, Gradle keeps this handle alive between build iterations and calls
 * [stop] when the invocation is cancelled. The same lifecycle also supports a directly blocking
 * session when automatic builds are disabled.
 */
open class GdxTeaVMDevServerDeployment @Inject constructor(
    internal val devServerProjectPath: String,
    val entryServerPort: Int,
    initialIndexHtml: ByteArray
) : DeploymentHandle {
    private val indexHtml = AtomicReference(initialIndexHtml.copyOf())
    private val running = AtomicBoolean()
    private var entryServer: HttpServer? = null

    override fun isRunning(): Boolean = running.get()

    override fun start(deployment: Deployment) {
        startSession()
    }

    internal fun startStandalone() {
        startSession()
    }

    private fun startSession() {
        if(!running.compareAndSet(false, true)) {
            return
        }

        try {
            entryServer = startEntryServer()
        }
        catch(t: Throwable) {
            running.set(false)
            stopTeaVMServer()
            throw t
        }
    }

    fun update(projectPath: String, expectedEntryPort: Int, content: ByteArray) {
        if(projectPath != devServerProjectPath) {
            throw GradleException(
                "TeaVM development-server project changed from '$devServerProjectPath' to '$projectPath'"
            )
        }
        if(expectedEntryPort != entryServerPort) {
            throw GradleException(
                "TeaVM development entry port changed from $entryServerPort to $expectedEntryPort"
            )
        }
        indexHtml.set(content.copyOf())
    }

    override fun stop() {
        if(!running.compareAndSet(true, false)) {
            return
        }
        entryServer?.stop(0)
        entryServer = null
        stopTeaVMServer()
        logger.lifecycle("TeaVM development server stopped.")
    }

    private fun stopTeaVMServer() {
        DevServerManager.instance().getProjectManager(devServerProjectPath).stop(logger)
    }

    private fun startEntryServer(): HttpServer {
        val address = InetSocketAddress(InetAddress.getByName(LOOPBACK_ADDRESS), entryServerPort)
        val server = HttpServer.create(address, 0)
        try {
            server.createContext("/") { exchange -> serveEntry(exchange) }
            server.start()
            return server
        }
        catch(t: Throwable) {
            server.stop(0)
            throw GradleException("Could not start the gdx-teavm entry server on $address", t)
        }
    }

    private fun serveEntry(exchange: HttpExchange) {
        try {
            val method = exchange.requestMethod
            if(method != "GET" && method != "HEAD") {
                exchange.responseHeaders.set("Allow", "GET, HEAD")
                exchange.sendResponseHeaders(HttpURLConnection.HTTP_BAD_METHOD, -1)
                return
            }

            when(exchange.requestURI.path) {
                "/" -> {
                    val content = indexHtml.get()
                    exchange.responseHeaders.set("Content-Type", "text/html; charset=utf-8")
                    exchange.responseHeaders.set("Cache-Control", "no-cache, no-store, must-revalidate")
                    if(method == "HEAD") {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, -1)
                    }
                    else {
                        exchange.sendResponseHeaders(HttpURLConnection.HTTP_OK, content.size.toLong())
                        exchange.responseBody.write(content)
                    }
                }
                "/index.html", "/webapp", "/webapp/", "/webapp/index.html" -> {
                    exchange.responseHeaders.set("Location", "/")
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_MOVED_TEMP, -1)
                }
                else -> exchange.sendResponseHeaders(HttpURLConnection.HTTP_NOT_FOUND, -1)
            }
        }
        finally {
            exchange.close()
        }
    }

    private companion object {
        val logger = Logging.getLogger(GdxTeaVMDevServerDeployment::class.java)
        const val LOOPBACK_ADDRESS = "127.0.0.1"
    }
}
