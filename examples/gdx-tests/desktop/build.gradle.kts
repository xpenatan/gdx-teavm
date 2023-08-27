//import org.gradle.internal.os.OperatingSystem

dependencies {
//    implementation(project(":examples:gdx-tests:core")
    // need to use lwjgl2 because lwjgl3 call makeCurrent and replace Gdx.input used inside gdx wrapper.
//    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
//
//    implementation("com.github.xpenatan.gdx-imgui:core-desktop:${LibExt.gdxImGuiVersion}")
//
//    implementation("com.badlogicgames.gdx:gdx-bullet-platform:${LibExt.gdxVersion}:natives-desktop")
//    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
}

//project.ext.mainClassName = "com.github.xpenatan.imgui.example.tests.Main"
//
//// Change to your source asset directory
//project.ext.assetsDir = new File("D:\\Dev\\Projects\\java\\Libgdx\\tests\\gdx-tests-android\\assets\\");
//
//tasks.register('runGdxTestsDesktop', JavaExec) {
//    dependsOn classes
//    setGroup("examples-desktop")
//    setDescription("Run gdx tests example")
//    mainClass.set(project.mainClassName)
//    setClasspath(sourceSets.main.runtimeClasspath)
//    workingDir = project.assetsDir
//
//    if (OperatingSystem.current() == OperatingSystem.MAC_OS) {
//        // Required to run on macOS
//        jvmArgs += "-XstartOnFirstThread"
//    }
//}