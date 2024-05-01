import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"

    val libVersion: String = getVersion()

    const val gdxVersion = "1.12.1"
    const val teaVMVersion = "0.10.0"

    const val gdxImGuiVersion = "1.0.0-SNAPSHOT"
    const val gdxMultiViewVersion = "1.0.0-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val aiVersion = "1.8.2"
}

private fun getVersion(): String {
    val isReleaseStr = System.getenv("RELEASE")
    val isRelease = isReleaseStr != null && isReleaseStr.toBoolean()
    var libVersion = "-SNAPSHOT"
    val file = File("gradle.properties")
    if(file.exists()) {
        val properties = Properties()
        properties.load(file.inputStream())
        val version = properties.getProperty("version")
        if(isRelease) {
            libVersion = version
        }
    }
    else {
        if(isRelease) {
            throw RuntimeException("properties should exist")
        }
    }
    println("Lib Version: $libVersion")
    return libVersion
}