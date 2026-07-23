dependencies {
    implementation(libs.gdxCore)
//    implementation(variantOf(libs.gdxCore) { classifier("sources") })
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

tasks.register<JavaExec>("basic_web_run") {
    group = "example-web"
    description = "Run basic example"
    mainClass.set("BuildTeaVMTestDemo")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("basic_visui_web_run") {
    group = "example-web"
    description = "Run basic VisUI example"
    mainClass.set("BuildTeaVMVisUI")
    classpath = sourceSets["main"].runtimeClasspath
}
