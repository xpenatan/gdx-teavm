import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"

    var libVersion = ""

    const val gdxVersion = "1.12.1"
    const val teaVMVersion = "0.10.2"

    const val gdxImGuiVersion = "-SNAPSHOT"
    const val gdxMultiViewVersion = "-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val gdxGltfVersion = "2.2.1"
    const val aiVersion = "1.8.2"

    const val truthVersion = "1.4.2"

    var gdxSourcePath = ""
    var gdxTestsAssetsPath = ""
    var teavmPath = ""
    var includeLibgdxSource = false
    var includeTeaVMSource = false

    fun initProperties(rootDir: File) {
        val properties = getProperties(rootDir)
        updateProperties(properties)
    }

    private fun updateProperties(properties: Properties) {
        val isVersionEmpty = libVersion.isEmpty()
        libVersion = getVersion(properties)

        if(isVersionEmpty) {
            println("Lib Version: $libVersion")
        }

        gdxSourcePath = properties.getOrDefault("gdxSourcePath", "") as String
        gdxTestsAssetsPath = "${gdxSourcePath}/tests/gdx-tests-android/assets/"
        teavmPath = properties.getOrDefault("teavmPath", "") as String
        includeLibgdxSource = (properties.getOrDefault("includeLibgdxSource", "false") as String).toBoolean()
        includeTeaVMSource = (properties.getOrDefault("includeTeaVMSource", "false") as String).toBoolean()
    }

    private fun getProperties(rootDir: File? = null): Properties {
        val propFile ="gradle.properties"
        val file = if(rootDir != null) {
            File(rootDir, propFile)
        } else {
            File(propFile)
        }
        val properties = Properties()
        if(file.exists()) {
            properties.load(file.inputStream())
        }
        updateProperties(properties)
        return properties
    }

    private fun getVersion(properties: Properties): String {
        val isReleaseStr = System.getenv("RELEASE")
        val isRelease = isReleaseStr != null && isReleaseStr.toBoolean()
        var libVersion = "-SNAPSHOT"
        val version = properties.getProperty("version", "")
        if(isRelease) {
            libVersion = version
        }
        return libVersion
    }
}