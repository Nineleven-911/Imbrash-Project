plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

dependencies {
    // Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
    implementation(libs.bundles.kotlinxEcosystem)
    testImplementation(kotlin("test"))
}

//////////
version = "Indev-1.0.0-F"

tasks.jar {
    archiveBaseName.set("IUtil")
}

fun getJarName(): String {
    return "${tasks.jar.get().archiveBaseName.get()}-${tasks.jar.get().archiveVersion.get()}.jar"
}

project.copy {
    if (File("${rootDir.path}\\official\\bin\\${getJarName()}").exists()) {
        File("${rootDir.path}\\official\\bin\\${getJarName()}").delete()
    }
    from("${project.projectDir}\\build\\libs\\${getJarName()}")
    into("${rootDir.path}\\official\\bin")
}
//////
