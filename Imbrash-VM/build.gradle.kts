fun projectModule(name: String) {
    dependencies {
        implementation(project(name))
    }
}

plugins {
    kotlin("jvm")
}

group = "hairinne.ip.vm"
version = "0.1.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    projectModule(":utils")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(23)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "hairinne.ip.vm.MainKt"
        attributes["Class-Path"] = listOf(
            "jk-rt\\kotlin-stdlib-2.0.21.jar",
            "jk-rt\\kotlinx-coroutines-core-jvm-1.6.4.jar",
            "jk-rt\\kotlinx-serialization-json-jvm-1.6.2.jar",
            "jk-rt\\kotlin-reflect-2.0.21.jar",
            "IUtil-Indev-1.0.0-F.jar"
        ).joinToString(" ")
    }
}

//////////
version = "Indev-1.0.0"

tasks.jar {
    archiveBaseName.set("Imbrash-VM")
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
