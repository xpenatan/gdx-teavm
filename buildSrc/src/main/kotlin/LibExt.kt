import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"
    const val libName = "gdx-teavm"
    var isRelease = false
    var libVersion: String = ""
        get() {
            return getVersion()
        }

    const val gdxVersion = "1.14.0"
    const val teaVMVersion = "0.13.0-dev-12"
    const val jMultiplatform = "0.1.3"

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

    private fun getVersion(): String {
        var libVersion = "-SNAPSHOT"
        val file = File("gradle.properties")
        if(file.exists()) {
            val properties = Properties()
            properties.load(file.inputStream())
            val version = properties.getProperty("version")
            if(LibExt.isRelease) {
                libVersion = version
            }
        }
        else {
            if(LibExt.isRelease) {
                throw RuntimeException("properties should exist")
            }
        }
        return libVersion
    }
}