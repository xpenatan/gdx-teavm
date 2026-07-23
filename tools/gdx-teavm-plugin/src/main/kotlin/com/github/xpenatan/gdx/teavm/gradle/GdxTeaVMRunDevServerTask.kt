package com.github.xpenatan.gdx.teavm.gradle

import com.sun.net.httpserver.HttpExchange
import com.sun.net.httpserver.HttpServer
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.logging.slf4j.ContextAwareTaskLogger
import org.gradle.process.ExecOperations
import org.gradle.work.DisableCachingByDefault
import org.teavm.gradle.tasks.DevServerManager
import org.teavm.gradle.tasks.DevServerTask
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.URI
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

internal const val GDX_TEAVM_DEV_SERVER_INDEX_FILE = "__gdx_teavm_index.html"
internal const val TEAVM_DEV_SERVER_STDERR_PREFIX = "server stderr:"
private const val DEV_SERVER_STATUS_BORDER = "#################################################################"
private const val FILE_WATCH_POLL_MILLIS = 250L
private const val FILE_WATCH_QUIET_MILLIS = 300L
private val DEV_SERVER_TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss")

internal fun devServerStatusMessage(port: Int, watching: Boolean = true): String {
    val lines = mutableListOf(
        DEV_SERVER_STATUS_BORDER,
        "|",
        "| TeaVM development server URL: http://localhost:$port"
    )
    if(watching) {
        lines.add("| Watching for changes...")
    }
    lines.add("|")
    lines.add(DEV_SERVER_STATUS_BORDER)
    return lines.joinToString("\n")
}

internal fun changeDetectedMessage(time: LocalTime = LocalTime.now()): String {
    return "[${time.format(DEV_SERVER_TIME_FORMATTER)}] Change detected. Rebuilding..."
}

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

internal fun suppressTeaVMRebuildNoise(level: LogLevel, message: String): String? {
    return if(level == LogLevel.WARN && message.startsWith(TEAVM_DEV_SERVER_STDERR_PREFIX)) {
        null
    }
    else {
        message
    }
}

@DisableCachingByDefault(because = "Runs a persistent TeaVM development-server session")
abstract class GdxTeaVMRunDevServerTask : DefaultTask() {
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

    @get:Input
    abstract val rebuildTaskPath: Property<String>

    @get:Internal
    abstract val watchFiles: ConfigurableFileCollection

    @get:Inject
    protected abstract val execOperations: ExecOperations

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

        val session = GdxTeaVMDevServerSession(
            devServerProjectPath.get(),
            entryServerPort.get(),
            indexHtml
        )
        val shutdownHook = Thread(session::stop, "gdx-teavm-dev-server-shutdown")
        Runtime.getRuntime().addShutdownHook(shutdownHook)
        try {
            session.start()
            logger.error(devServerStatusMessage(port.get(), autoBuild.get()))
            if(autoBuild.get()) {
                suppressRebuildNoise()
                watchAndRebuild(manager, session)
            }
            else {
                CountDownLatch(1).await()
            }
        }
        finally {
            session.stop()
            removeShutdownHook(shutdownHook)
        }
    }

    private fun suppressRebuildNoise() {
        val contextLogger = logger as? ContextAwareTaskLogger ?: return
        contextLogger.setMessageRewriter(::suppressTeaVMRebuildNoise)
    }

    private fun watchAndRebuild(
        manager: org.teavm.gradle.tasks.ProjectDevServerManager,
        session: GdxTeaVMDevServerSession
    ) {
        val watcher = GdxTeaVMFileWatcher(watchFiles.files)
        while(true) {
            watcher.awaitChange()
            logger.lifecycle(changeDetectedMessage())
            val started = System.nanoTime()
            try {
                compileProjectClasses()
                manager.runBuild(logger, null)
                session.update(loadIndexHtml())
                logger.lifecycle("Rebuild successful in ${formatDuration(System.nanoTime() - started)}.")
            }
            catch(e: InterruptedException) {
                throw e
            }
            catch(t: Throwable) {
                if(t is GdxTeaVMRebuildException && t.details.isNotBlank()) {
                    logger.error(t.details)
                }
                logger.lifecycle("Rebuild failed. Waiting for changes...")
                logger.debug("TeaVM development-server rebuild failed", t)
            }
        }
    }

    private fun compileProjectClasses() {
        val output = ByteArrayOutputStream()
        val result = execOperations.exec {
            commandLine(gradleCommand())
            workingDir(project.rootDir)
            standardOutput = output
            errorOutput = output
            isIgnoreExitValue = true
        }
        if(result.exitValue != 0) {
            throw GdxTeaVMRebuildException(output.toString(Charsets.UTF_8).trim())
        }
    }

    private fun gradleCommand(): List<String> {
        val gradleHome = project.gradle.gradleHomeDir
        val windows = System.getProperty("os.name", "").startsWith("Windows", ignoreCase = true)
        val executable = File(gradleHome, if(windows) "bin/gradle.bat" else "bin/gradle")
        if(!executable.isFile) {
            throw GradleException("Could not find the Gradle executable at ${executable.absolutePath}")
        }

        val command = mutableListOf<String>()
        if(windows) {
            command.add(System.getenv("ComSpec") ?: "cmd.exe")
            command.add("/d")
            command.add("/c")
        }
        command.add(executable.absolutePath)
        command.add("--project-dir")
        command.add(project.rootDir.absolutePath)
        command.add("--quiet")
        command.add("--console=plain")
        if(project.gradle.startParameter.isOffline) {
            command.add("--offline")
        }
        for((key, value) in project.gradle.startParameter.projectProperties.toSortedMap()) {
            command.add("-P$key=$value")
        }
        command.add(rebuildTaskPath.get())
        return command
    }

    private fun formatDuration(nanos: Long): String {
        val millis = nanos / 1_000_000
        return if(millis < 1_000) {
            "${millis}ms"
        }
        else {
            String.format(Locale.ROOT, "%.1fs", millis / 1_000.0)
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

private class GdxTeaVMRebuildException(val details: String) : RuntimeException()

internal data class GdxTeaVMFileStamp(
    val modifiedMillis: Long,
    val size: Long
)

internal class GdxTeaVMFileWatcher(
    roots: Collection<File>,
    private val pollMillis: Long = FILE_WATCH_POLL_MILLIS,
    private val quietMillis: Long = FILE_WATCH_QUIET_MILLIS
) {
    private val roots = roots.mapTo(linkedSetOf()) { file ->
        file.toPath().toAbsolutePath().normalize()
    }
    private var snapshot = snapshot()

    fun awaitChange() {
        while(true) {
            Thread.sleep(pollMillis)
            var current = snapshot()
            if(current == snapshot) {
                continue
            }

            do {
                snapshot = current
                Thread.sleep(quietMillis)
                current = snapshot()
            }
            while(current != snapshot)
            snapshot = current
            return
        }
    }

    private fun snapshot(): Map<Path, GdxTeaVMFileStamp> {
        val result = linkedMapOf<Path, GdxTeaVMFileStamp>()
        for(root in roots) {
            if(Files.isRegularFile(root)) {
                addFile(root, result)
            }
            else if(Files.isDirectory(root)) {
                try {
                    Files.walk(root).use { paths ->
                        paths.filter(Files::isRegularFile).forEach { path ->
                            addFile(path, result)
                        }
                    }
                }
                catch(_: IOException) {
                    // A safe-save operation can replace a directory while it is being scanned.
                }
            }
        }
        return result
    }

    private fun addFile(path: Path, result: MutableMap<Path, GdxTeaVMFileStamp>) {
        try {
            val attributes = Files.readAttributes(path, BasicFileAttributes::class.java)
            result[path.toAbsolutePath().normalize()] = GdxTeaVMFileStamp(
                attributes.lastModifiedTime().toMillis(),
                attributes.size()
            )
        }
        catch(_: IOException) {
            // The next poll observes files that were replaced while this snapshot was collected.
        }
    }
}

internal class GdxTeaVMDevServerSession(
    internal val devServerProjectPath: String,
    val entryServerPort: Int,
    initialIndexHtml: ByteArray
) {
    private val indexHtml = AtomicReference(initialIndexHtml.copyOf())
    private val running = AtomicBoolean()
    private var entryServer: HttpServer? = null

    fun start() {
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

    fun update(content: ByteArray) {
        indexHtml.set(content.copyOf())
    }

    fun stop() {
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
        val logger = Logging.getLogger(GdxTeaVMDevServerSession::class.java)
        const val LOOPBACK_ADDRESS = "127.0.0.1"
    }
}
