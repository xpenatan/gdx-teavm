import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"

    val libVersion: String = getVersion()

    const val gdxVersion = "1.12.1"
    const val teaVMVersion = "0.9.2"

    const val gdxImGuiVersion = "1.0.0-SNAPSHOT"
    const val gdxMultiViewVersion = "1.0.0-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val aiVersion = "1.8.2"
}

private fun getVersion(): String {
    val file = File("gradle.properties")
    val properties = Properties()
    properties.load(file.inputStream())
    val version = properties.getProperty("version")

    val isRelease = System.getenv("RELEASE")
    var libVersion = "-SNAPSHOT"
    if(isRelease != null && isRelease.toBoolean()) {
        libVersion = version
    }
    System.out.println("gdx-teavm Version: $libVersion")
    return libVersion
}