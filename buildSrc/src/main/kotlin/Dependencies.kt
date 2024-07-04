import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"

    val properties = getProperties();

    val libVersion: String = getVersion(properties)

    const val gdxVersion = "1.12.1"
    const val teaVMVersion = "0.10.0"

    const val gdxImGuiVersion = "-SNAPSHOT"
    const val gdxMultiViewVersion = "-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val aiVersion = "1.8.2"

    const val truthVersion = "1.4.2"


    // ######### Add libgdx tests to project
    // ######### Need to have libgdx project source code tag 1.12.1.
    // ######### Need to disable in libgdx settings :tests:gdx-tests-gwt because of some conflicts
    // ######### Need to update gradle properties.
    val gdxSourcePath = properties.getOrDefault("gdxSourcePath", "") as String
    val gdxTestsAssetsPath = "${gdxSourcePath}/tests/gdx-tests-android/assets/"
    val teavmPath = properties.getOrDefault("teavmPath", "") as String
    val includeLibgdxSource = (properties.getOrDefault("includeLibgdxSource", "false") as String).toBoolean()
    val includeTeaVMSource = (properties.getOrDefault("includeTeaVMSource", "false") as String).toBoolean()
}

private fun getVersion(properties: Properties): String {
    val isReleaseStr = System.getenv("RELEASE")
    val isRelease = isReleaseStr != null && isReleaseStr.toBoolean()
    var libVersion = "-SNAPSHOT"
    val version = properties.getProperty("version")
    if(isRelease) {
        libVersion = version
    }
    println("Lib Version: $libVersion")
    return libVersion
}

private fun getProperties(): Properties {
    val file = File("gradle.properties")
    val properties = Properties()
    if(file.exists()) {
        properties.load(file.inputStream())
    }
    else {
        throw RuntimeException("properties should exist")
    }
    return properties
}