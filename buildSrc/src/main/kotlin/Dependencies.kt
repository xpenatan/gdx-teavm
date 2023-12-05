object LibExt {
    const val groupId = "com.github.xpenatan.gdx-teavm"
    val libVersion: String = getVersion("1.0.0", "b9")

    const val gdxVersion = "1.12.1"
    const val teaVMVersion = "0.10.0-dev-2"

    const val gdxImGuiVersion = "1.0.0-SNAPSHOT"
    const val gdxMultiViewVersion = "1.0.0-SNAPSHOT"

    const val reflectionVersion = "0.10.2"
    const val jettyVersion = "11.0.13"

    const val aiVersion = "1.8.2"
}

private fun getVersion(releaseVersion: String, suffix: String = ""): String {
    val isRelease = System.getenv("RELEASE")
    var libVersion = "${releaseVersion}-SNAPSHOT"
    if(isRelease != null && isRelease.toBoolean()) {
        libVersion = releaseVersion + if(suffix.isNotEmpty()) "-${suffix}" else ""
    }
    System.out.println("Gdx-teaVM Version: " + libVersion)
    return libVersion
}