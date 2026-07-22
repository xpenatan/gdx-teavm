import java.io.File
import java.util.*

object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"
    const val libName = "gdx-teavm"
    const val snapshotVersion = "-SNAPSHOT"
    var isRelease = false
    val releaseVersion: String
        get() = readReleaseVersion()
    var libVersion: String = ""
        get() {
            return if(isRelease) releaseVersion else snapshotVersion
        }

    const val gdxVersion = "1.14.2"
    const val teaVMVersion = "0.15.0"
    const val jMultiplatform = "0.1.3"

    const val gdxImGuiVersion = "-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val gdxGltfVersion = "2.3.0"
    const val aiVersion = "1.8.2"
    const val gdxControllerVersion = "2.2.4"

    const val truthVersion = "1.4.2"

    var gdxSourcePath = ""
    var gdxTestsAssetsPath = ""
    var teavmPath = ""
    var includeLibgdxSource = false
    var includeTeaVMSource = false
    private var propertiesRootDir: File? = null

    fun initProperties(rootDir: File) {
        propertiesRootDir = rootDir
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

    private fun readReleaseVersion(): String {
        val file = propertiesRootDir?.resolve("gradle.properties") ?: File("gradle.properties")
        if(file.exists()) {
            val properties = Properties()
            properties.load(file.inputStream())
            return properties.getProperty("version")
                ?: throw RuntimeException("version property should exist")
        }
        throw RuntimeException("properties should exist")
    }
}
